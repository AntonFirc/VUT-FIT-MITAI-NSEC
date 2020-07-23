/* KRY projekt 2 2020
 * RSA
 * Anton Firc (xfirca00)
 */
#include "kry.hpp"

using namespace std;

void printHelp() {
    cout << "-------------------- Usage ------------------" << endl << 
    "Generate keys:" << endl << "command: \"./kry -g B\"" << endl << "output: \"P Q N E D\"" << endl <<
    "Encrypt: " << endl << "command: \"./kry -e E N M\"" << endl << "output: \"C\"" << endl <<
    "Decrypt: " << endl << "command: \"./kry -d D N C\"" << endl << "output: \"M\"" << endl <<
    "Break: " << endl << "command: \" ./kry -b E N C\"" << endl << "output: \"P Q M\"" << endl <<
    " - B...requested size of public modulus in b" << endl <<
    " - P...prime number (random when -g used)" << endl <<
    " - Q...prime number (random when -g used)" << endl <<
    " - N...public modulus" << endl <<
    " - E...public exponent" << endl <<
    " - D...private exponent" << endl <<
    " - M...cleartext message (number, not string)" << endl <<
    " - C...ciphertext message (number, not string)" << endl <<
    " - All numbers (except B) are hexadecimal and begin with 0x" << endl;

}

int main(int argc, char** argv) {

    if ( (argc < 3) || (argc > 5)) {
        printHelp();
        return EXIT_FAILURE;
    }

    string  command = argv[1];

    if ( (command == "-g") && (argc == 3) ) {
        int bitLen = stoi(argv[2]);

        if (bitLen < 6) {
            cerr << "Key length must be more than 6 bits.";
            return EXIT_FAILURE;
        } 

        KeyGenerator::generateKey(bitLen);

    }
    else if ( (command == "-e") && (argc == 5) ) {
        mpz_t message, exponent, modulus;

        mpz_init(message);
        mpz_init(exponent);
        mpz_init(modulus);

        mpz_set_str(exponent, argv[2], 0);
        mpz_set_str(modulus, argv[3], 0);
        mpz_set_str(message, argv[4], 0);

        CipherModule::cipher(message, exponent, modulus);
    }
    else if ( (command == "-d") && (argc == 5) ) {
        mpz_t cipherText, exponent, modulus;

        mpz_init(cipherText);
        mpz_init(exponent);
        mpz_init(modulus);

        mpz_set_str(exponent, argv[2], 0);
        mpz_set_str(modulus, argv[3], 0);
        mpz_set_str(cipherText, argv[4], 0);

        CipherModule::decipher(cipherText, exponent, modulus);
    }
    else if ( (command == "-b") && (argc == 5) ) {
        mpz_t cipherText, exponent, modulus;

        mpz_init(cipherText);
        mpz_init(exponent);
        mpz_init(modulus);

        mpz_set_str(exponent, argv[2], 0);
        mpz_set_str(modulus, argv[3], 0);
        mpz_set_str(cipherText, argv[4], 0);

        RSABreaker::breakRSA(exponent, modulus, cipherText);
    }
    else {
        printHelp();
    }

    return EXIT_SUCCESS;

}