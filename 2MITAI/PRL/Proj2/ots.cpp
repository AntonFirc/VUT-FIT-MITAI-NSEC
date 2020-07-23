/**
 * Projekt 1 PRL 2020
 * Odd-Even Transposition sort
 * Anton Firc (xfirca00)
 */

#include <mpi.h>
#include <iostream>
#include <fstream>
#include <vector>

#ifdef BENCHMARK
#include "papi_cntr.h"
#include <locale>
#endif

using namespace std;

int main (int argc, char** argv) {
    
    int proc_count;
    int my_rank;
    const int MPI_TAG = 0;
    MPI_Status status;
    int my_num; // number assigned to process

    // initialize MPI
    MPI_Init(&argc,&argv);

    MPI_Comm_size(MPI_COMM_WORLD, &proc_count); // number of running processes
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);    // process rank
    
    #ifdef BENCHMARK
        if (my_rank == 0) printf("Benchmark mode ON, using PAPI\n");
        float real_time, proc_time, ipc = 0.0;
        long long int ins = 0;
    #endif

    // make main process (rank=0) load all values to be sorted and distribute them to other processes
    if (my_rank == 0) {
        
        short loaded = 0; //keep track of loaded numbers count

        ifstream input("numbers");
        if(!input)  // operator! is used here
        {  
            std::cout << "File opening failed\n";
            return EXIT_FAILURE;
        }
        
        // read input file until EOF
        while (input.good()) {

            int num = input.get();

            if (!input.eof()) {

                loaded++; //keep track of loaded numbers count
                
                // if loaded numbers count does not match process count
                if (loaded > proc_count) {
                    cerr << "Input size \"" << loaded << "\" does not match expected size \"" << proc_count << "\" (process count)" << endl;
                    return EXIT_FAILURE;
                }
                
                cout << num << ' ';

                if (loaded == 1) {
                    my_num = num;
                }
                else {
                    //send number to process: (message, message_size, message_type, receiver_rank, TAG, communicator)
                    MPI_Send(&num, 1, MPI_INT, loaded-1, MPI_TAG, MPI_COMM_WORLD);
                }
            }
        }
        input.close();
        cout << endl;

    }
    else {
        //receive number: (message , message_size, messgae_type, sender_rank, TAG, communicator, status)
        MPI_Recv(&my_num, 1, MPI_INT, 0, MPI_TAG, MPI_COMM_WORLD, &status);
    }  
    
    int n_num; // number received from neighbor with smaller rank

    #ifdef BENCHMARK
        PAPI_ipc(&real_time, &proc_time, &ins, &ipc);
    #endif
    
    // all processes (including main) swap values with "right" neighbor in cycle
    for (int i = 0; i <= (proc_count+1) / 2; i++) {
        int even_max = ((proc_count-1) /2) *2;
        int odd_max = (proc_count/2) *2 -1;

        /****************SORTING EVEN PROCESSES*************************************/
        // all even processes except the "last" one without right neighbor
        if (((my_rank == 0) || (my_rank%2 == 0)) && (my_rank < odd_max)) {
            MPI_Send(&my_num, 1, MPI_INT, my_rank+1, MPI_TAG, MPI_COMM_WORLD);
            MPI_Recv(&my_num, 1, MPI_INT, my_rank+1, MPI_TAG, MPI_COMM_WORLD, &status);
        }
        // all odd processes listen for number from left even neighbor and respond with smaller number
        else if (my_rank <= odd_max) {
            MPI_Recv(&n_num, 1, MPI_INT, my_rank-1, MPI_TAG, MPI_COMM_WORLD, &status);

            if (my_num < n_num) {
                MPI_Send(&my_num, 1, MPI_INT, my_rank-1, MPI_TAG, MPI_COMM_WORLD);
                my_num = n_num;
            }
            else {
                MPI_Send(&n_num, 1, MPI_INT, my_rank-1, MPI_TAG, MPI_COMM_WORLD);
            }
        }

        /********************SORTING ODD PROCESSES**********************************/
        // all odd processes except the "last" one without right neighbor
        if ((my_rank%2 == 1) && (my_rank < even_max)) {
            MPI_Send(&my_num, 1, MPI_INT, my_rank+1, MPI_TAG, MPI_COMM_WORLD);
            MPI_Recv(&my_num, 1, MPI_INT, my_rank+1, MPI_TAG, MPI_COMM_WORLD, &status);
        }
        // all odd processes (except first one) listen for number from left even neighbor and respond with smaller number
        else if ((my_rank <= even_max) && (my_rank != 0)) {
            MPI_Recv(&n_num, 1, MPI_INT, my_rank-1, MPI_TAG, MPI_COMM_WORLD, &status);

            if (my_num < n_num) {
                MPI_Send(&my_num, 1, MPI_INT, my_rank-1, MPI_TAG, MPI_COMM_WORLD);
                my_num = n_num;
            }
            else {
                MPI_Send(&n_num, 1, MPI_INT, my_rank-1, MPI_TAG, MPI_COMM_WORLD);
            }
        }
         
    }

    #ifdef BENCHMARK
        PAPI_ipc(&real_time, &proc_time, &ins, &ipc);
        printf("Proc %d => %d\n", my_rank, ins);
    #endif  

    // distribute result from processes to main process
    if (my_rank != 0) {
        MPI_Send(&my_num, 1, MPI_INT, 0, MPI_TAG, MPI_COMM_WORLD);

        #ifdef BENCHMARK
        MPI_Send(&ins, 1, MPI_INT, 0, MPI_TAG, MPI_COMM_WORLD);
        #endif
    }
    // main process gathers all results and prints them to stdout
    else {
        cout << my_num << endl;
        for (int i = 1; i < proc_count; i++) {
            int num;
            int ins_r = 0;
            MPI_Recv(&num, 1, MPI_INT, i, MPI_TAG, MPI_COMM_WORLD, &status);
            #ifdef BENCHMARK
                MPI_Recv(&ins_r, 1, MPI_INT, i, MPI_TAG, MPI_COMM_WORLD, &status);
                ins += ins_r;
            #endif
            cout << num << endl;
        }
        #ifdef BENCHMARK
            std::ofstream outins;
            std::stringstream ssi;
            ssi << "benchmark/instructions" << proc_count << ".csv";
            std::string fileins = ssi.str();
            outins.open(fileins, std::ios_base::app); // append instead of overwrite
            outins << ins << "\n"; 
            cout << "Total instructions = " << ins << endl;
            std::ofstream outtime;
            std::stringstream sst;
            sst << "benchmark/time" << proc_count << ".csv";
            std::string filetime = sst.str();
            outtime.open(filetime, std::ios_base::app); // append instead of overwrite
            outtime <<  fixed << real_time << endl;
            cout << "Total time: " << fixed << real_time << endl;
        #endif
    }

    MPI_Finalize();

    return EXIT_SUCCESS;
}