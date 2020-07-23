#include "KeyGenerator.hpp"

void KeyGenerator::generateKey(int bitLen) {

    mpz_t p, q, n, phi, p_1, q_1, e, d;

    mpz_init(p);
    mpz_init(q);
    mpz_init(n);
    mpz_init(phi);
    mpz_init(p_1);
    mpz_init(q_1);
    mpz_init(e);
    mpz_init(d);

    gmp_randstate_t state;
    gmp_randinit_mt(state);
    gmp_randseed_ui(state, hash3(time(NULL), clock(), getpid()));

    /* set public exponent according to public key modulus in bits */
    if ( bitLen < 2048) {
        mpz_set_ui(e, 3);
    }
    else
    {
        mpz_set_ui(e, 65537);
    }

    do {
        getPrimes(p, q, bitLen, state);
        mpz_sub_ui(p_1, p, 1);
        mpz_sub_ui(q_1, q, 1);
        mpz_mul(phi, p_1, q_1);

    } while ( gcd(e, phi) != 1 );
   
    multiplicativeInverse(d, e, phi);

    mpz_mul(n, p, q);

    gmp_printf("0x%Zx 0x%Zx 0x%Zx 0x%Zx 0x%Zx\n",p, q, n, e, d);

}

bool KeyGenerator::isPrime(mpz_t primeSuspect, int bitLen, gmp_randstate_t state) {

    mpz_t tmp, tmp2, maxVal, a;
    mpz_init(tmp);
    mpz_init(tmp2);
    mpz_init(maxVal);
    mpz_init(a);

    mpz_t n_1, exp, left, expCounter;
    mpz_init(n_1);
    mpz_init(exp);
    mpz_init(left);
    mpz_init(expCounter);

    mpz_t jacobi, right;
    mpz_init(jacobi);
    mpz_init(right);

    int numLen = bitLen/2;

    if (mpz_cmp_ui(primeSuspect, 2) < 0) {
        return false;
    }

    mpz_mod_ui(tmp, primeSuspect,2);
    if (mpz_cmp_ui(primeSuspect, 2) && !mpz_cmp_ui(tmp, 0)) {
        return false;
    }

    for (int i = 0; i < 50; i++) {
        /* generate random number from <2; n-1>*/
        mpz_urandomb(tmp, state, numLen);
        mpz_sub_ui(maxVal, primeSuspect, 1);
        mpz_mod(a, tmp, maxVal);
        mpz_add_ui(a, a, 1);

        /* calculate left side of Solovay-Strassen method equation */
        mpz_t test;
        mpz_t y;
        mpz_init_set_ui(test, 1);
        mpz_init_set(y, a);
        // n-1
        mpz_sub_ui(exp, primeSuspect, 1);
        // (n-1)/2
        mpz_div_ui(exp, exp, 2);

        mpz_powm(left, a, exp, primeSuspect);


        /* calculate the right side of Solovay-Strassen method equation */
        int x = jacobiSymbol(a , primeSuspect);

        if (x == 0) {
            return false;
        }
        else if ( x == -1 ) {
            mpz_sub_ui(right, primeSuspect, 1);
        }
        else {
             mpz_set_ui(jacobi, x);
             mpz_mod(right, jacobi, primeSuspect);
        }

        if ( mpz_cmp(left, right) ) {
            return false;
        }

    }

    mpz_clear(tmp);
    mpz_clear(maxVal);
    mpz_clear(a);
    mpz_clear(exp);
    mpz_clear(left);
    mpz_clear(right);
    mpz_clear(jacobi);

    // probably prime number
    return true;

}

void KeyGenerator::getPrimes(mpz_t p, mpz_t q, int bitLen, gmp_randstate_t state) {

    getRandoms(p, q, bitLen, state);

    while (!isPrime(p, bitLen,state)) {
        mpz_add_ui(p, p, 2);
    }

    while (!isPrime(q, bitLen, state)) {
        mpz_add_ui(q, q, 2);
    }


};

void KeyGenerator::getRandoms(mpz_t p, mpz_t q, int bitLen, gmp_randstate_t state) {

    int pLen = bitLen / 2;
    int qLen = bitLen - pLen; 

    mpz_urandomb(p, state, pLen);
    // make generated numbers odd
    mpz_setbit(p, 0);

    do {
        mpz_urandomb(q, state, qLen);
        mpz_setbit(q, 0);
    } while (!mpz_cmp(p, q));

};

unsigned long KeyGenerator::hash3(unsigned long p1, unsigned long p2, unsigned long p3) {
    return ((p1 * 2654435789UL) + p2) * 2654435789UL + p3;
};

int KeyGenerator::jacobiSymbol(mpz_t aIn, mpz_t nIn) {

    mpz_t a, n, tmpMod1, tmpMod2, tmp;
    mpz_init(a);
    mpz_set(a, aIn);
    mpz_init(n);
    mpz_set(n, nIn);
   
    mpz_init(tmpMod1);
    mpz_init(tmpMod2);
    mpz_init(tmp);

    if (!mpz_cmp_ui(a, 0) ) {
        return 0;
    }

    int j;
    j = 1;

    if (mpz_cmp_ui(a, 0) < 0) {
        mpz_neg(a, a);
        mpz_mod_ui(tmpMod1, n, 4);
        if (!mpz_cmp_ui(tmpMod1, 3)) {
            j = -j;
        }
    }

    if (!mpz_cmp_ui(a, 1)){
        return j;
    }

    while (mpz_cmp_ui(a, 0)) {
        if (mpz_cmp_ui(a,0) < 0) {
            mpz_neg(a, a);
            mpz_mod_ui(tmpMod1, n, 4);
            if (!mpz_cmp_ui(tmpMod1, 3)) {
                j = -j;
            }
        }
        mpz_mod_ui(tmpMod1, a, 2);
        while (!mpz_cmp_ui(tmpMod1, 0)) {
            /* Process factors of 2: Jacobi(2,b)=-1 if b=3,5 (mod 8) */
            mpz_div_ui(a, a, 2);
            mpz_mod_ui(tmpMod1, n, 8);
            if ( (!mpz_cmp_ui(tmpMod1,3)) || (!mpz_cmp_ui(tmpMod1,5)) ) {
                j = -j;
            }
            mpz_mod_ui(tmpMod1, a, 2);
        }
        /* Quadratic reciprocity: Jacobi(a,b)=-Jacobi(b,a) if a=3,b=3 (mod 4) */
        //interchange(a,n)
        mpz_set(tmp, a);
        mpz_set(a, n);
        mpz_set(n, tmp);
        mpz_mod_ui(tmpMod1, a, 4);
        mpz_mod_ui(tmpMod2, n, 4);
        if ( (!mpz_cmp_ui(tmpMod1, 3)) && (!mpz_cmp_ui(tmpMod2, 3)) ) {
            j = -j;
        }
        mpz_mod(a, a, n);
        mpz_div_ui(tmpMod1, n, 2);
        if (mpz_cmp(a, tmpMod1) > 0) {
            mpz_sub(a, a, n);
        }
    }

    if ( !mpz_cmp_ui(n, 1) ) {
        return j;
    }
    else {
        return 0;
    }

}

void KeyGenerator::multiplicativeInverse(mpz_t d, mpz_t e, mpz_t phi){
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

int KeyGenerator::gcd(mpz_t a, mpz_t b) {
    
    mpz_t x;
    mpz_t y;
    mpz_t gcd;

    mpz_init(x);
    mpz_init(y);
    mpz_init(gcd);

    mpz_set(x, a);
    mpz_set(y, b);

    while( mpz_cmp_ui(y, 0) ) {
        mpz_set(gcd, y);
        mpz_mod(y, x, y);
        mpz_set(x,gcd);
    }

    return mpz_get_ui(gcd);

}

bool KeyGenerator::testPrime(mpz_t prime, int length){
    int i = 0;
    int opLength = length / 2;
    
    // init of random generator
    unsigned long seed;
    gmp_randstate_t rstate;
    gmp_randinit_mt(rstate);
    gmp_randseed_ui(rstate, seed);
    
    // init random
    mpz_t veryRandomNumber; mpz_init(veryRandomNumber);
    // random a
    mpz_t a; mpz_init(a);
    // left side
    mpz_t leftCongruent; mpz_init(leftCongruent);
    // right side
    mpz_t rightCongruent; mpz_init(rightCongruent);
    // helper var
    mpz_t helper; mpz_init(helper);
    
    // prime < 2
    if (mpz_cmp_ui(prime, 2) < 0){
        return false;
    }
    
    // prime % 2
    mpz_mod_ui(helper, prime, 2);
    
    // prime != 2 || prime % 2 == 0
    if (mpz_cmp_ui(prime, 2) != 0 && mpz_cmp_ui(helper, 0) == 0){
        return false;
    }

    // repeat multiple times to increase probability
    while(i < 50)
    {
        i++;
        /* generate random a smaller than prime */
        mpz_urandomb(veryRandomNumber, rstate, opLength);
        mpz_sub_ui(helper, prime, 1);
        mpz_mod(a, veryRandomNumber, helper);
        mpz_add_ui(a, a, 1);
        
        /* get left side of congruence */
        mpz_t x; mpz_init_set_ui(x, 1);
        mpz_t y; mpz_init_set(y, a);
        mpz_t exponent; mpz_init(exponent);
        mpz_sub_ui(exponent, prime, 1);
        mpz_div_ui(exponent, exponent, 2);
        
        // modular exponentiation
        while(mpz_cmp_ui(exponent, 0) > 0)
        {
            mpz_mod_ui(helper, exponent, 2);
            if (mpz_cmp_ui(helper, 1) == 0){
                mpz_mul(helper, x, y);
                mpz_mod(x, helper, prime);
            }
            mpz_mul(helper, y, y);
            mpz_mod(y, helper, prime);
            mpz_div_ui(exponent, exponent, 2);
        }
        mpz_mod(leftCongruent, x, prime);
        
        /* get right side of congruence */
        // get jacob numb
        int myJac = jacobiSymbol(a, prime);
        mpz_t jacobVar; mpz_init(jacobVar);
        if (myJac < 0){
            mpz_set_ui(jacobVar, -myJac);
            
        }else{
            
        }
        mpz_mod(rightCongruent, jacobVar, prime);
        
        /* are left and right side in congruence? */
        if (mpz_cmp_ui(rightCongruent, 0) == 0 || mpz_cmp(rightCongruent, leftCongruent) != 0)
        {
            return false;
        }
    }
    mpz_clear(veryRandomNumber);
    mpz_clear(leftCongruent);
    mpz_clear(rightCongruent);
    mpz_clear(a);
    mpz_clear(helper);

    return true;
}