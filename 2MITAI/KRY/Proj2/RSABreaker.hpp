/* KRY projekt 2 2020
 * RSA
 * Anton Firc (xfirca00)
 */
#include <gmp.h>
#include <iostream> 

using namespace std;

class RSABreaker {

    public:
        static void breakRSA(mpz_t exponent, mpz_t modulus, mpz_t cipherText);

    private:
         static void gcd(mpz_t gcd, mpz_t a, mpz_t b);
         static void factorizePollard(mpz_t p, mpz_t modulus);
         static void multiplicativeInverse(mpz_t d, mpz_t e, mpz_t phi);
        

};