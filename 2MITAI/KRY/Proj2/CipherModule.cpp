/* KRY projekt 2 2020
 * RSA
 * Anton Firc (xfirca00)
 */
#include "CipherModule.hpp"

void CipherModule::cipher(mpz_t message, mpz_t exponent, mpz_t modulus) {
    mpz_t cipherText;
    mpz_init(cipherText);

    mpz_powm(cipherText, message, exponent, modulus);

    gmp_printf("0x%Zx\n",cipherText);
}

void CipherModule::decipher(mpz_t cipherText, mpz_t exponent, mpz_t modulus) {
    mpz_t message;
    mpz_init(message);

    mpz_powm(message, cipherText, exponent, modulus);

    gmp_printf("0x%Zx\n",message);
}