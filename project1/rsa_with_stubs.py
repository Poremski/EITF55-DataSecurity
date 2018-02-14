#!/usr/bin/python
#
# Prime generation part modified fom Rosetta
#

import sys
import random
import math

num_bases_to_test = 20 # number of bases to test

def try_composite(a, d, n, s):
    if pow(a, d, n) == 1:
        return False
    for i in range(s):
        if pow(a, 2**i * d, n) == n-1:
            return False
    return True # n is definitely composite

"""
    Miller-Rabin primality test.
 
    A return value of False means n is certainly not prime. A return value of
    True means n is very likely a prime.
 
    >>> is_miller_rabin_prime(5)
    True
    >>> is_miller_rabin_prime(123456789)
    False
 
    >>> primes_under_1000 = [i for i in range(2, 1000) if is_miller_rabin_prime(i)]
    >>> len(primes_under_1000)
    168
    >>> primes_under_1000[-10:]
    [937, 941, 947, 953, 967, 971, 977, 983, 991, 997]
 
    >>> is_miller_rabin_prime(6438080068035544392301298549614926991513861075340134\
3291807343952413826484237063006136971539473913409092293733259038472039\
7133335969549256322620979036686633213903952966175107096769180017646161\
851573147596390153)
    True
 
    >>> is_miller_rabin_prime(7438080068035544392301298549614926991513861075340134\
3291807343952413826484237063006136971539473913409092293733259038472039\
7133335969549256322620979036686633213903952966175107096769180017646161\
851573147596390153)
    False
"""
def is_miller_rabin_prime(n):
    assert n >= 2
    if n in (2,3,5,7): return True
    if any(n % a == 0 for a in (2,3,5,7)): return False
    # write n-1 as 2**s * d
    # repeatedly try to divide n-1 by 2
    s = 0
    d = n-1
    while True:
        quotient, remainder = divmod(d, 2)
        if remainder == 1:
            break
        s += 1
        d = quotient
    assert(2**s * d == n-1)

    # use random base a several times to test for compositeness
    for i in range(num_bases_to_test):
        a = random.randrange(2, n)
        if try_composite(a,d,n,s):
            return False

    return True # no base tested showed n as composite

def is_prime(n, _precision_for_huge_n=20):
    if n in _known_primes or n in (0, 1):
        return True
    if any((n % p) == 0 for p in _known_primes):
        return False
    d, s = n - 1, 0
    while not d % 2:
        d, s = d >> 1, s + 1
    # Returns exact according to http://primes.utm.edu/prove/prove2_3.html
    if n < 1373653: return not any(try_composite(a, d, n, s) for a in (2, 3))
    if n < 25326001: return not any(try_composite(a, d, n, s) for a in (2, 3, 5))
    if n < 118670087467: 
        if n == 3215031751: return False
        return not any(try_composite(a, d, n, s) for a in (2, 3, 5, 7))
    if n < 2152302898747: return not any(try_composite(a, d, n, s) for a in (2, 3, 5, 7, 11))
    if n < 3474749660383: return not any(try_composite(a, d, n, s) for a in (2, 3, 5, 7, 11, 13))
    if n < 341550071728321: return not any(try_composite(a, d, n, s) for a in (2, 3, 5, 7, 11, 13, 17))
    # otherwise
    return not any(try_composite(a, d, n, s) for a in _known_primes[:_precision_for_huge_n])

_known_primes = [2, 3]
_known_primes += [x for x in range(5, 1000, 2) if is_prime(x)]

def generate_probable_prime(num_bits):
  while True:
    p = random.randint(2**(num_bits-1)+1, 2**num_bits - 1)
#    if is_prime(p): # uses sequential basis (2,3,5,7,11,13,17,...
    if is_miller_rabin_prime(p): # uses random basis
      return p

# integers a, b as input
# return triplet (g, x, y), such that ax + by = g = gcd(a, b)
def extended_gcd(a, b):
  # not implemented, return g,x,y (in that order)
  g = 0 # not implemented
  x = 0 # not implemented
  y = 0 # not implemented
  return g,x,y

# input: integers a and m with 1<a<m
# output: inverse of a modulo m if it exists (in which case a * a_inverse = 1 mod m), 0 otherwise
def modinv(a, m):
  g, x, y = extended_gcd(a, m)
  if g != 1: return 0
  return x % m

def generate_rsa_parameters(e, num_bits):
  # not implemented, return p,q,n,d (in that order)
  p = 3 # not implemented, generate prime p
  q = 5 # not implemented, generate prime q
  n = 15 # not implemented, compute n
  d = 2 # not implemented, compute d (e is given as parameter)
  assert p!=q  # p and q must be different primes
  assert d!=0  # e must have an inverse modulo n
  return p,q,n,d

def test_rsa_encryption_decryption(n,e,d):
  plaintext = random.randint(2,n-1)
  ciphertext = 0 # not implemented, compute ciphertext from plaintext
  recovered_plaintext = 0 # not implemented, compute plaintext from ciphertext
  #assert plaintext == recovered_plaintext
  print("\nTesting encryption/decryption")
  print("plaintext = %d" % plaintext)
  print("ciphertext = %d" % ciphertext)
  print("recovered plaintext = %d" % recovered_plaintext)
  if (plaintext == recovered_plaintext):
    print("Encryption/decryption successful\n")
  else:
    print("Encryption/decryption failed!\n")

def test_rsa_prime_generation(num_bits):
  print("Generating %d-bit prime..." % num_bits, end="")
  sys.stdout.flush()
  p = generate_probable_prime(NUM_BITS)
  print("done")
  print("p = %d" % p)

def test_rsa_parameter_generation(num_bits):
  print("Generating %d-bit RSA parameters..." % num_bits, end="")
  sys.stdout.flush()
  e = 65537 # currently the "best" choice
  p,q,n,d = generate_rsa_parameters(e, num_bits)
  print("done")
  print("p = %d" % p)
  print("q = %d" % q)
  print("n = %d" % n)
  print("e = %d" % e)
  print("d = %d" % d)
  test_rsa_encryption_decryption(n,e,d)

#
# Main program
#
NUM_BITS = 512
test_rsa_prime_generation(NUM_BITS)
#test_rsa_parameter_generation(NUM_BITS)
