/* KRY projekt 2 2020
 * RSA
 * Anton Firc (xfirca00)
 */
#include <gmp.h>
#include <unistd.h> 
#include <iostream> 

using namespace std;

class KeyGenerator {
    public:
        static void generateKey(int bitLen);
        

    private:
        static bool isPrime(mpz_t primeSuspect, int bitLen, gmp_randstate_t state);
        static bool testPrime(mpz_t prime, int bitLen);
        static void getPrimes(mpz_t p, mpz_t q, int bitLen, gmp_randstate_t state);
        static void getRandoms(mpz_t p, mpz_t q, int bitLen, gmp_randstate_t state);
        static unsigned long hash3(unsigned long p1, unsigned long p2, unsigned long p3);
        static int jacobiSymbol(mpz_t aIn, mpz_t nIn);
        static void multiplicativeInverse(mpz_t d, mpz_t e, mpz_t phi);
        static int gcd(mpz_t a, mpz_t b);
};