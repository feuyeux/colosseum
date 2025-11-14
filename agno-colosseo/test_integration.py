#!/usr/bin/env python3
"""
Integration tests for Agno Colosseo service.
Tests all endpoints, configuration loading, and error handling.
"""

import asyncio
import sys
import httpx
from typing import Dict, Any

# Test configuration
BASE_URL = "http://localhost:60900"
TIMEOUT = 30.0

class Colors:
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    END = '\033[0m'

def print_test(name: str):
    print(f"\n{Colors.BLUE}Testing: {name}{Colors.END}")

def print_success(message: str):
    print(f"{Colors.GREEN}✓ {message}{Colors.END}")

def print_error(message: str):
    print(f"{Colors.RED}✗ {message}{Colors.END}")

def print_warning(message: str):
    print(f"{Colors.YELLOW}⚠ {message}{Colors.END}")

async def test_health_check() -> bool:
    """Test health check endpoint"""
    print_test("Health Check Endpoint")
    try:
        async with httpx.AsyncClient(timeout=TIMEOUT) as client:
            response = await client.get(f"{BASE_URL}/health")
            
            if response.status_code == 200:
                data = response.json()
                if data.get("status") in ["healthy", "degraded"]:
                    print_success(f"Health check passed: {data}")
                    return True
                else:
                    print_error(f"Unexpected health status: {data}")
                    return False
            else:
                print_error(f"Health check failed with status {response.status_code}")
                return False
    except Exception as e:
        print_error(f"Health check error: {e}")
        return False

async def test_supported_languages() -> bool:
    """Test supported languages endpoint"""
    print_test("Supported Languages Endpoint")
    try:
        async with httpx.AsyncClient(timeout=TIMEOUT) as client:
            response = await client.get(f"{BASE_URL}/api/languages")
            
            if response.status_code == 200:
                languages = response.json()
                if isinstance(languages, dict) and len(languages) > 0:
                    print_success(f"Retrieved {len(languages)} supported languages")
                    print(f"  Languages: {', '.join(languages.keys())}")
                    return True
                else:
                    print_error("Invalid languages response format")
                    return False
            else:
                print_error(f"Languages endpoint failed with status {response.status_code}")
                return False
    except Exception as e:
        print_error(f"Languages endpoint error: {e}")
        return False

async def test_single_language_translation() -> bool:
    """Test translation with single target language"""
    print_test("Single Language Translation")
    try:
        async with httpx.AsyncClient(timeout=TIMEOUT) as client:
            request_data = {
                "sourceText": "Hello, how are you?",
                "targetLanguages": ["es"]
            }
            response = await client.post(
                f"{BASE_URL}/api/translateText",
                json=request_data
            )
            
            if response.status_code == 200:
                result = response.json()
                if "translations" in result and "es" in result["translations"]:
                    translation = result["translations"]["es"]
                    print_success(f"Translation successful: '{request_data['sourceText']}' -> '{translation}'")
                    return True
                else:
                    print_error(f"Invalid translation response: {result}")
                    return False
            else:
                print_error(f"Translation failed with status {response.status_code}: {response.text}")
                return False
    except Exception as e:
        print_error(f"Translation error: {e}")
        return False

async def test_multiple_language_translation() -> bool:
    """Test translation with multiple target languages"""
    print_test("Multiple Language Translation")
    try:
        async with httpx.AsyncClient(timeout=TIMEOUT) as client:
            request_data = {
                "sourceText": "Good morning",
                "targetLanguages": ["es", "fr", "de"]
            }
            response = await client.post(
                f"{BASE_URL}/api/translateText",
                json=request_data
            )
            
            if response.status_code == 200:
                result = response.json()
                translations = result.get("translations", {})
                
                if len(translations) == 3:
                    print_success(f"Translated to {len(translations)} languages:")
                    for lang, text in translations.items():
                        print(f"  {lang}: {text}")
                    return True
                else:
                    print_error(f"Expected 3 translations, got {len(translations)}")
                    return False
            else:
                print_error(f"Multiple translation failed with status {response.status_code}")
                return False
    except Exception as e:
        print_error(f"Multiple translation error: {e}")
        return False

async def test_grammar_analysis() -> bool:
    """Test grammar analysis endpoint"""
    print_test("Grammar Analysis Endpoint")
    try:
        async with httpx.AsyncClient(timeout=TIMEOUT) as client:
            request_data = {
                "text": "The quick brown fox jumps over the lazy dog",
                "languageCode": "en"
            }
            response = await client.post(
                f"{BASE_URL}/api/analyzeGrammar",
                json=request_data
            )
            
            if response.status_code == 200:
                result = response.json()
                components = result.get("components", [])
                
                if len(components) > 0:
                    print_success(f"Analysis returned {len(components)} grammar components")
                    # Show first few components
                    for comp in components[:3]:
                        print(f"  {comp.get('text')}: {comp.get('componentType')}")
                    return True
                else:
                    print_error("No grammar components returned")
                    return False
            else:
                print_error(f"Grammar analysis failed with status {response.status_code}")
                return False
    except Exception as e:
        print_error(f"Grammar analysis error: {e}")
        return False

async def test_invalid_translation_request() -> bool:
    """Test error handling for invalid translation requests"""
    print_test("Invalid Translation Request Handling")
    
    test_cases = [
        {
            "name": "Empty source text",
            "data": {"sourceText": "", "targetLanguages": ["es"]},
            "expected_status": [400, 422]  # FastAPI returns 422 for validation errors
        },
        {
            "name": "No target languages",
            "data": {"sourceText": "Hello", "targetLanguages": []},
            "expected_status": [400, 422]
        },
        {
            "name": "Unsupported language",
            "data": {"sourceText": "Hello", "targetLanguages": ["xyz"]},
            "expected_status": [400, 422]
        },
        {
            "name": "Text too long",
            "data": {"sourceText": "a" * 10001, "targetLanguages": ["es"]},
            "expected_status": [400, 422]
        }
    ]
    
    all_passed = True
    async with httpx.AsyncClient(timeout=TIMEOUT) as client:
        for test_case in test_cases:
            try:
                response = await client.post(
                    f"{BASE_URL}/api/translateText",
                    json=test_case["data"]
                )
                
                expected = test_case["expected_status"]
                if isinstance(expected, list):
                    if response.status_code in expected:
                        print_success(f"{test_case['name']}: Correctly rejected")
                    else:
                        print_error(f"{test_case['name']}: Expected one of {expected}, got {response.status_code}")
                        all_passed = False
                else:
                    if response.status_code == expected:
                        print_success(f"{test_case['name']}: Correctly rejected")
                    else:
                        print_error(f"{test_case['name']}: Expected {expected}, got {response.status_code}")
                        all_passed = False
            except Exception as e:
                print_error(f"{test_case['name']}: Error - {e}")
                all_passed = False
    
    return all_passed

async def test_invalid_analysis_request() -> bool:
    """Test error handling for invalid analysis requests"""
    print_test("Invalid Analysis Request Handling")
    
    test_cases = [
        {
            "name": "Empty text",
            "data": {"text": "", "languageCode": "en"},
            "expected_status": [400, 422]  # FastAPI returns 422 for validation errors
        },
        {
            "name": "Unsupported language",
            "data": {"text": "Hello", "languageCode": "xyz"},
            "expected_status": [400, 422]
        },
        {
            "name": "Text too long",
            "data": {"text": "a" * 10001, "languageCode": "en"},
            "expected_status": [400, 422]
        }
    ]
    
    all_passed = True
    async with httpx.AsyncClient(timeout=TIMEOUT) as client:
        for test_case in test_cases:
            try:
                response = await client.post(
                    f"{BASE_URL}/api/analyzeGrammar",
                    json=test_case["data"]
                )
                
                expected = test_case["expected_status"]
                if isinstance(expected, list):
                    if response.status_code in expected:
                        print_success(f"{test_case['name']}: Correctly rejected")
                    else:
                        print_error(f"{test_case['name']}: Expected one of {expected}, got {response.status_code}")
                        all_passed = False
                else:
                    if response.status_code == expected:
                        print_success(f"{test_case['name']}: Correctly rejected")
                    else:
                        print_error(f"{test_case['name']}: Expected {expected}, got {response.status_code}")
                        all_passed = False
            except Exception as e:
                print_error(f"{test_case['name']}: Error - {e}")
                all_passed = False
    
    return all_passed

async def test_static_files() -> bool:
    """Test static file serving"""
    print_test("Static File Serving")
    
    all_passed = True
    async with httpx.AsyncClient(timeout=TIMEOUT) as client:
        # Test root path
        try:
            response = await client.get(f"{BASE_URL}/")
            if response.status_code == 200 and "html" in response.headers.get("content-type", ""):
                print_success("Root path serves HTML")
            else:
                print_error(f"Root path failed: status {response.status_code}")
                all_passed = False
        except Exception as e:
            print_error(f"Root path error: {e}")
            all_passed = False
        
        # Test static CSS
        try:
            response = await client.get(f"{BASE_URL}/static/css/styles.css")
            if response.status_code == 200:
                print_success("Static CSS file accessible")
            else:
                print_error(f"CSS file failed: status {response.status_code}")
                all_passed = False
        except Exception as e:
            print_error(f"CSS file error: {e}")
            all_passed = False
        
        # Test static JS
        try:
            response = await client.get(f"{BASE_URL}/static/js/translation.js")
            if response.status_code == 200:
                print_success("Static JS file accessible")
            else:
                print_error(f"JS file failed: status {response.status_code}")
                all_passed = False
        except Exception as e:
            print_error(f"JS file error: {e}")
            all_passed = False
    
    return all_passed

async def test_cors_headers() -> bool:
    """Test CORS configuration"""
    print_test("CORS Configuration")
    try:
        async with httpx.AsyncClient(timeout=TIMEOUT) as client:
            # Send OPTIONS request to check CORS
            response = await client.options(
                f"{BASE_URL}/api/translateText",
                headers={
                    "Origin": "http://example.com",
                    "Access-Control-Request-Method": "POST"
                }
            )
            
            cors_headers = {
                "access-control-allow-origin": response.headers.get("access-control-allow-origin"),
                "access-control-allow-methods": response.headers.get("access-control-allow-methods"),
                "access-control-allow-headers": response.headers.get("access-control-allow-headers")
            }
            
            if cors_headers["access-control-allow-origin"]:
                print_success(f"CORS enabled: {cors_headers}")
                return True
            else:
                print_error("CORS headers not found")
                return False
    except Exception as e:
        print_error(f"CORS test error: {e}")
        return False

async def test_configuration_loading() -> bool:
    """Test that configuration files are properly loaded"""
    print_test("Configuration Loading")
    try:
        # Import config module to verify it loads without errors
        import config
        
        # Check required configuration values
        required_configs = [
            "OLLAMA_BASE_URL",
            "OLLAMA_MODEL_NAME",
            "OLLAMA_TIMEOUT",
            "MAX_TEXT_LENGTH",
            "SUPPORTED_LANGUAGES",
            "LANGUAGE_NAMES",
            "SERVER_HOST",
            "SERVER_PORT",
            "TRANSLATION_PROMPT",
            "ANALYSIS_PROMPT"
        ]
        
        all_present = True
        for config_name in required_configs:
            if hasattr(config, config_name):
                value = getattr(config, config_name)
                if value is not None:
                    print_success(f"{config_name}: {value if len(str(value)) < 50 else str(value)[:50] + '...'}")
                else:
                    print_error(f"{config_name} is None")
                    all_present = False
            else:
                print_error(f"{config_name} not found in config")
                all_present = False
        
        return all_present
    except Exception as e:
        print_error(f"Configuration loading error: {e}")
        return False

async def run_all_tests():
    """Run all integration tests"""
    print(f"\n{Colors.BLUE}{'='*60}")
    print("Agno Colosseo Integration Tests")
    print(f"{'='*60}{Colors.END}\n")
    
    results = {}
    
    # Test configuration loading first
    results["Configuration Loading"] = await test_configuration_loading()
    
    # Test health and connectivity
    results["Health Check"] = await test_health_check()
    
    # Test API endpoints
    results["Supported Languages"] = await test_supported_languages()
    results["Single Language Translation"] = await test_single_language_translation()
    results["Multiple Language Translation"] = await test_multiple_language_translation()
    results["Grammar Analysis"] = await test_grammar_analysis()
    
    # Test error handling
    results["Invalid Translation Requests"] = await test_invalid_translation_request()
    results["Invalid Analysis Requests"] = await test_invalid_analysis_request()
    
    # Test static files and CORS
    results["Static File Serving"] = await test_static_files()
    results["CORS Configuration"] = await test_cors_headers()
    
    # Print summary
    print(f"\n{Colors.BLUE}{'='*60}")
    print("Test Summary")
    print(f"{'='*60}{Colors.END}\n")
    
    passed = sum(1 for result in results.values() if result)
    total = len(results)
    
    for test_name, result in results.items():
        status = f"{Colors.GREEN}PASS{Colors.END}" if result else f"{Colors.RED}FAIL{Colors.END}"
        print(f"{test_name}: {status}")
    
    print(f"\n{Colors.BLUE}Total: {passed}/{total} tests passed{Colors.END}")
    
    if passed == total:
        print(f"{Colors.GREEN}All tests passed!{Colors.END}\n")
        return 0
    else:
        print(f"{Colors.RED}Some tests failed.{Colors.END}\n")
        return 1

if __name__ == "__main__":
    exit_code = asyncio.run(run_all_tests())
    sys.exit(exit_code)
