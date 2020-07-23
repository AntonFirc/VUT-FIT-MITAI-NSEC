/* KRY projekt 2 2020
 * RSA
 * Anton Firc (xfirca00)
 */
#include "RSABreaker.hpp"

void RSABreaker::breakRSA(mpz_t exponent, mpz_t modulus, mpz_t cipherText) {

    mpz_t p, q, d, p_1, q_1, phi, message;
    mpz_init(p);
    mpz_init(q);
    mpz_init(d);
    mpz_init(p_1);
    mpz_init(q_1);
    mpz_init(phi);
    mpz_init(message);

    factorizePollard(p, modulus);

    if ( !mpz_cmp(p, modulus) ) {
        cerr << "Factorization failed !" << endl;
    }

    mpz_div(q, modulus, p);

    mpz_sub_ui(p_1, p, 1);
    mpz_sub_ui(q_1, q, 1);
    mpz_mul(phi, p_1, q_1);

    multiplicativeInverse(d, exponent, phi);

    mpz_powm(message, cipherText, d, modulus);

    gmp_printf("0x%Zx 0x%Zx 0x%Zx\n",p, q, message);
}

void RSABreaker::factorizePollard(mpz_t p, mpz_t modulus) {

    mpz_t x, y, size, factor, count, tmp;
    mpz_init(x);
    mpz_init(y);
    mpz_init(size);
    mpz_init(factor);
    mpz_init(count);
    mpz_init(tmp);

    mpz_set_ui(x, 2);
    mpz_set_ui(y, 2);
    mpz_set_ui(size, 2);
    mpz_set_ui(factor, 1);

    // g(x) = (x * x + 1) % modulus;
    do {
        mpz_set(count, size);
        do {
            // x = g(x)
            mpz_mul(x, x, x);
            mpz_add_ui(x, x, 1);
            mpz_mod(x, x, modulus);

            // y = g(y)
            mpz_mul(y, y, y);
            mpz_add_ui(y, y, 1);
            mpz_mod(y, y, modulus);
            // y = g(g(y))
            mpz_mul(y, y, y);
            mpz_add_ui(y, y, 1);
            mpz_mod(y, y, modulus);

            //factor = gcd(abs(x - y), modulus);
            mpz_sub(tmp, x, y);
            mpz_abs(tmp, tmp);
            gcd(factor, tmp, modulus);
            
            mpz_sub_ui(count, count, 1);
        } while ( (mpz_cmp_ui(count, 0) > 0) && (!mpz_cmp_ui(factor, 1)) );
        mpz_mul_ui(size, size, 2);
    } while ( !mpz_cmp_ui(factor, 1) );

    mpz_set(p, factor);
}

void RSABreaker::gcd(mpz_t gcd, mpz_t a, mpz_t b) {
    
    mpz_t x;
    mpz_t y;

    mpz_init(x);
    mpz_init(y);

    mpz_set(x, a);
    mpz_set(y, b);

    while( mpz_cmp_ui(y, 0) ) {
        mpz_set(gcd, y);
        mpz_mod(y, x, y);
        mpz_set(x,gcd);
    }

}

void RSABreaker::multiplicativeInverse(mpz_t d, mpz_t e, mpz_t phi) {
    mpz_t y;
    mpz_t x;
    mpz_t m;
    mpz_t a;

    mpz_init(y);
    mpz_init(x);
    mpz_init(m);
    mpz_init(a);

    mpz_set_ui(y, 0);
    mpz_set_ui(x, 1);
    mpz_set(m, phi);
    mpz_set(a, e);

    mpz_t xDef; 
    mpz_t yDef; 
    mpz_init_set_ui(xDef, 1);
    mpz_init_set_ui(yDef, 0);
    
    mpz_t q; 
    mpz_t tmp1; 
    mpz_t tmp2; 
    mpz_init(q);
    mpz_init(tmp1);
    mpz_init(tmp2);

    if ( !mpz_cmp_ui(m, 1) ) {
        mpz_set_ui(d, 0);
    }

    while(mpz_cmp_ui(m, 0) != 0){
        mpz_div(q, a, m);
        mpz_set(tmp1, m);
        
        mpz_mul(tmp2, q, tmp1);
        mpz_sub(m, a, tmp2);
        mpz_set(a, tmp1);

        mpz_set(tmp1, y);
        mpz_mul(tmp2, q, tmp1);
        mpz_sub(y, xDef, tmp2);
        mpz_set(xDef, tmp1);
        
        mpz_set(tmp1, x);
        mpz_mul(tmp2, q, tmp1);
        mpz_sub(x, yDef, tmp2);
        mpz_set(yDef, tmp1);
    }
    
    mpz_set(d, xDef);
    
    if (mpz_cmp_ui(d, 0) < 0){
        mpz_add(d, d, phi);
    }
    
}