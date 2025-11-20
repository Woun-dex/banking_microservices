import argparse
import asyncio
import aiohttp
import time
import uuid
from typing import Optional
from tqdm.asyncio import tqdm_asyncio


OPTIMISTIC_KEYWORDS = [
    "optimis", "optimistic", "StaleObjectStateException", "OptimisticLockException",
    "Row was updated or deleted", "lock", "concurrent", "version"
]

def looks_like_optimistic(text: str) -> bool:
    """Check if error text contains optimistic locking keywords"""
    lower_text = text.lower()
    return any(keyword.lower() in lower_text for keyword in OPTIMISTIC_KEYWORDS)

async def post_json(session: aiohttp.ClientSession, url: str, json_data: dict, headers: dict) -> dict:
    async with session.post(url , json=json_data, headers=headers) as response:
        text = await response.text()
        return response.status, text
    

async def get_json(session: aiohttp.ClientSession, url: str, headers: dict) -> dict:
    async with session.get(url, headers=headers) as response:
        text = await response.text()
        return response.status, text
    


async def create_user_and_get_token(session: aiohttp.ClientSession , base_url: str , username: str, password: str) -> Optional[str]:
    create_user_url = f"{base_url}/api/users/user"
    token_url = f"{base_url}/api/users/auth/token"
    
    # Add timestamp to username to make it unique
    import time
    unique_username = f"{username}_{int(time.time())}"
    
    user_data = {
        "username": unique_username,
        "email": f"{unique_username}@example.com",
        "password": password,
        "role": "CUSTOMER"
    }
    
    status, response_text = await post_json(session, create_user_url, user_data, headers={})
    
    user_id = None
    if status == 201:
        # User created successfully, parse userId from response
        try:
            import json
            user_response = json.loads(response_text)
            user_id = user_response.get("id") or user_response.get("userId")
            print(f"[+] User created successfully. Username: {unique_username}, User ID: {user_id}")
        except Exception as e:
            print(f"Failed to parse user creation response: {e}")
            return None
    else:
        print(f"Failed to create user {unique_username}. Status code: {status}")
        print(f"Response: {response_text}")
        return None
    
    if not user_id:
        print("Could not extract user ID from response")
        return None
    
    # Now request token with userId and role
    token_data = {
        "userId": str(user_id),
        "role": "CUSTOMER"
    }
    
    status, response_text = await post_json(session, token_url, token_data, headers={})
    if status != 200:
        print(f"Failed to get token for user {unique_username}. Status code: {status}")
        print(f"Response: {response_text}")
        return None
    
    import json
    try:
        token_response = json.loads(response_text)
        # Try different possible token field names
        token = token_response.get("token") or token_response.get("access_token") or token_response.get("jwt")
        return token
    except Exception as e:
        print(f"Failed to parse token response: {e}")
        print(f"Response text: {response_text}")
        return None


async def create_account(session: aiohttp.ClientSession, base_url: str, token: Optional[str], user_id: str,
                        initial_balance: float) -> Optional[str]:
    create_account_url = f"{base_url}/api/accounts"
    headers= {}
    if token:
        # Ensure single Bearer prefix
        headers["Authorization"] = token if token.startswith("Bearer ") else f"Bearer {token}"
    # Debug: show token brief and outgoing headers
    try:
        print("[debug] create_account headers:", {k: (v[:20] + '...' if isinstance(v, str) and len(v) > 20 else v) for k, v in headers.items()})
    except Exception:
        pass
    
    payload = {
        "userId": str(user_id),
        "currency": "USD",
        "initialBalance": initial_balance
    }

    async with session.post(create_account_url, json=payload, headers=headers) as response:
        # capture response details for debugging
        text = await response.text()
        resp_headers = dict(response.headers)
        if response.status >= 300:
            print(f"[debug] create_account response headers: {resp_headers}")
            print(f"[debug] create_account response body: {text}")
            raise Exception(f"Failed to create account: {response.status} - {text}")
        try:
            data = await response.json()
        except Exception :
            raise RuntimeError(f"Failed to parse JSON response: {text}")
        
        return data.get("accountId")
    

async def get_account(session: aiohttp.ClientSession, base_url: str, token: Optional[str], account_id: str):
    url = f"{base_url}/api/accounts/{account_id}"
    headers = {}
    if token:
        headers["Authorization"] = token if token.startswith("Bearer ") else f"Bearer {token}"
    async with session.get(url, headers=headers) as resp:
        text = await resp.text()
        if resp.status >= 300:
            return resp.status, text
        try:
            j = await resp.json()
        except Exception:
            return resp.status, text
        return resp.status, j
    

async def do_transfer(session: aiohttp.ClientSession, base_url: str, token: Optional[str],
                      from_id: str, to_id: str, amount: float, txref: str):
    url = f"{base_url}/api/transactions/transfer"
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = token if token.startswith("Bearer ") else f"Bearer {token}"
    payload = {
        "transactionRef": txref,
        "fromAccountId": from_id,
        "toAccountId": to_id,
        "amount": amount
    }
    try:
        async with session.post(url, json=payload, headers=headers) as resp:
            text = await resp.text()
            return resp.status, text
    except Exception as e:
        return None, str(e)
    


async def run_test(args):
    base_url = args.base_url.rstrip("/")
    num = args.num
    amount = args.amount
    concurrency = args.concurrency

    timeout = aiohttp.ClientTimeout(total=60)
    conn = aiohttp.TCPConnector(limit=0, force_close=True)  # unlimited connections
    async with aiohttp.ClientSession(timeout=timeout, connector=conn) as session:

        token = None
        if args.create_user:
            print("[*] Creating user and obtaining token...")
            token = await create_user_and_get_token(session, base_url, args.username, args.password)
            if token:
                print("[+] Got token (truncated):", token[:60] if len(token) > 60 else token)
            else:
                print("[-] Failed to obtain token, exiting.")
                return

        # create or use existing accounts
        if args.create_accounts:
            print("[*] Creating two accounts with initial balance:", args.initial_balance)
            acc_a = await create_account(session, base_url, token, args.username, args.initial_balance)
            acc_b = await create_account(session, base_url, token, args.username, args.initial_balance)
            print("[+] Created accounts:", acc_a, acc_b)
        else:
            if not args.from_account or not args.to_account:
                raise RuntimeError("If --create-accounts is false you must provide --from-account and --to-account")
            acc_a = args.from_account
            acc_b = args.to_account

        # show initial balances
        st, bal_a = await get_account(session, base_url, token, acc_a)
        st, bal_b = await get_account(session, base_url, token, acc_b)
        print("[*] Initial balances:")
        print("   ", acc_a, bal_a)
        print("   ", acc_b, bal_b)

        # prepare tasks
        successes = 0
        failures = 0
        optimistic_conflicts = 0
        other_errors = {}
        results = []

        sem = asyncio.Semaphore(concurrency)

        async def worker(i: int):
            nonlocal successes, failures, optimistic_conflicts
            txref = str(uuid.uuid4())
            await sem.acquire()
            try:
                status, text = await do_transfer(session, base_url, token, acc_a, acc_b, amount, txref)
                results.append((status, text))
                if status and 200 <= status < 300:
                    successes += 1
                else:
                    failures += 1
                    txt = text or ""
                    if looks_like_optimistic(txt):
                        optimistic_conflicts += 1
                    else:
                        other_errors.setdefault(status or "exception", 0)
                        other_errors[status or "exception"] += 1
            finally:
                sem.release()

        print(f"[*] Launching {num} transfers concurrently with concurrency={concurrency} ...")
        started_at = time.perf_counter()
        # launch all tasks
        tasks = [asyncio.create_task(worker(i)) for i in range(num)]
        # progress bar
        await tqdm_asyncio.gather(*tasks)
        elapsed = time.perf_counter() - started_at

        # get final balances
        st_a, final_a = await get_account(session, base_url, token, acc_a)
        st_b, final_b = await get_account(session, base_url, token, acc_b)

        # compute expected final balance (assuming transfers are 1-way from A->B)
        # initial balance extraction: try to parse numeric from earlier responses if possible
        def parse_balance(node):
            if isinstance(node, dict):
                for k in ["balance", "initialBalance", "amount", "data"]:
                    v = node.get("balance") if node.get("balance") is not None else node.get("initialBalance")
                # try obvious keys
                if node.get("balance") is not None:
                    try:
                        return float(node.get("balance"))
                    except Exception:
                        pass
            # fallback: unknown
            return None

        # As a simple approach, we won't attempt to reparse the earlier responses reliably.
        # Instead, ask the user to provide initial balance as argument (we have args.initial_balance)
        initial_balance = args.initial_balance

        expected_successful = successes
        expected_final_a = initial_balance - expected_successful * amount
        expected_final_b = initial_balance + expected_successful * amount

        # Try to extract numeric final balances
        def extract_balance_from_response(resp):
            if isinstance(resp, dict):
                for key in ("balance", "bal", "amount", "initialBalance"):
                    if key in resp:
                        try:
                            return float(resp[key])
                        except Exception:
                            pass
                # nested 'data'
                if "data" in resp and isinstance(resp["data"], dict):
                    return extract_balance_from_response(resp["data"])
            return None

        bal_a_num = extract_balance_from_response(final_a) if isinstance(final_a, dict) else None
        bal_b_num = extract_balance_from_response(final_b) if isinstance(final_b, dict) else None

        # Report
        print("\n=== TEST REPORT ===")
        print(f"Total requested transfers : {num}")
        print(f"Successful (2xx)         : {successes}")
        print(f"Failed                  : {failures}")
        print(f"Optimistic-like conflicts: {optimistic_conflicts}")
        print(f"Other errors breakdown   : {other_errors}")
        print(f"Elapsed seconds         : {elapsed:.2f} s")
        print()
        print("Final balance responses (raw):")
        print("Account A:", final_a)
        print("Account B:", final_b)
        print()
        if bal_a_num is not None and bal_b_num is not None:
            print("Parsed final balances:")
            print(f" Account A: {bal_a_num} | expected: {expected_final_a}")
            print(f" Account B: {bal_b_num} | expected: {expected_final_b}")
            print("Balance correctness A:", abs(bal_a_num - expected_final_a) < 0.0001)
            print("Balance correctness B:", abs(bal_b_num - expected_final_b) < 0.0001)
        else:
            print("Could not parse numeric final balances automatically.")
            print("Expected final balances (given initial_balance argument):")
            print(f" Account A expected: {expected_final_a}")
            print(f" Account B expected: {expected_final_b}")

        return {
            "total": num,
            "successes": successes,
            "failures": failures,
            "optimistic_conflicts": optimistic_conflicts,
            "other_errors": other_errors,
            "elapsed": elapsed,
            "final_a_raw": final_a,
            "final_b_raw": final_b,
            "bal_a_num": bal_a_num,
            "bal_b_num": bal_b_num
        }


def main():
    parser = argparse.ArgumentParser(description="Concurrent transfer tester for Banking microservices")
    parser.add_argument("--base-url", default="http://localhost:8080", help="API Gateway base URL")
    parser.add_argument("--num", type=int, default=1000, help="Number of concurrent transfers to execute")
    parser.add_argument("--concurrency", type=int, default=500, help="Concurrency limit (simultaneous requests)")
    parser.add_argument("--amount", type=float, default=1.0, help="Transfer amount per request")
    parser.add_argument("--create-user", action="store_true", help="Create a test user and login to obtain token")
    parser.add_argument("--username", default="testuser", help="Username for created user")
    parser.add_argument("--password", default="P@ssw0rd!", help="Password for created user")
    parser.add_argument("--create-accounts", action="store_true", help="Create two accounts for the user")
    parser.add_argument("--initial-balance", type=float, default=200000.0, help="Initial balance for created accounts")
    parser.add_argument("--from-account", help="If not creating accounts, the from-account id")
    parser.add_argument("--to-account", help="If not creating accounts, the to-account id")
    parser.add_argument("--token", help="Provide an existing Bearer token (if present, --create-user is ignored)")
    args = parser.parse_args()

    # If token provided by CLI, use it directly
    if args.token:
        # ensure bearer prefix
        if not args.token.startswith("Bearer "):
            args.token = f"Bearer {args.token}"
        args.create_user = False

    asyncio.run(run_test(args))


if __name__ == "__main__":
    main()
