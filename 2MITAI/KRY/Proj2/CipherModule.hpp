/* KRY projekt 2 2020
 * RSA
 * Anton Firc (xfirca00)
 */
#include <gmp.h>
#include <iostream> 

class CipherModule {
    public:
        static void cipher(mpz_t message, mpz_t exponent, mpz_t modulus);
        static void decipher(mpz_t cipherText, mpz_t exponent, mpz_t modulus);

    private:

};