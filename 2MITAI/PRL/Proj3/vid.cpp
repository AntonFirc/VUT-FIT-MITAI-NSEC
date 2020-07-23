/**
 * Projekt 3 PRL 2020
 * Viditelnost
 * Anton Firc (xfirca00)
 */

#include <mpi.h>
#include <iostream>
#include <fstream>
#include <vector>
#include <math.h>
#include <float.h>
#include <algorithm>

#ifdef BENCHMARK
    #include <chrono>
    using namespace std::chrono;
    high_resolution_clock::time_point startTime, endTime;
#endif

using namespace std;

const int MPI_TAG = 0;
const int NO_VAL = -1;
const int INACTIVE_CPU = -2;

/**
 * Determines whether is parent node in imaginary binary tree created by processes
 *
 * @param int myRank -> index of curent process
 * @param int realCPUs -> how many physical CPUs are in use
 * @param int idx -> index of iteration in downSweep method
 *
 * @returns -> true = is parent node, otherwise false
 *
 */
bool parentNode(int myRank, int realCPUs, int idx) {
    for (int i = 0; i < realCPUs; i += pow(2, idx)) {
        if (myRank == (i + pow(2, idx)) -1)
            return true;
    }

    return false;
}

/**
 * Determines whether is child node in imaginary binary tree created by processes
 *
 * @param int myRank -> index of curent process
 * @param int realCPUs -> how many physical CPUs are in use
 * @param int idx -> index of iteration in downSweep method
 *
 * @returns -> true = is child node, otherwise false
 *
 */
bool childNode(int myRank, int realCPUs, int idx) {

    for (int i = 0; i < realCPUs; i += pow(2, idx)) {
        if (myRank == (i + pow(2, idx -1) -1))
            return true;
    }

    return false;
}

/**
 * Distribute max previous values to each process using pointers to maxPrevAngle(1/2)
 *
 * @param double* angleP1 -> pointer to maxPrevAngle1
 * @param double* angleP2 -> pointer to maxPrevAngle2
 * @param int myRank -> id of current process
 * @param int activeCPUs -> how many processes are active
 *
 */
void downSweep(double *angleP1, double *angleP2, int myRank, int activeCPUs) {

    MPI_Status status;

    for (int i = log2(activeCPUs) -1; i > 0; i--) {
        if (parentNode(myRank, activeCPUs/2, i)) {
            double childVal;
            MPI_Recv(&childVal, 1, MPI_DOUBLE, myRank - pow(2, i-1), MPI_TAG, MPI_COMM_WORLD, &status);
            MPI_Send(angleP2, 1, MPI_DOUBLE, myRank - pow(2, i-1), MPI_TAG, MPI_COMM_WORLD);
            *angleP2 = max(childVal, *angleP2);
        }
        else if (childNode(myRank, activeCPUs/2, i)) {
            MPI_Send(angleP2, 1, MPI_DOUBLE, myRank + pow(2, i-1), MPI_TAG, MPI_COMM_WORLD);
            MPI_Recv(angleP2, 1, MPI_DOUBLE, myRank + pow(2, i-1), MPI_TAG, MPI_COMM_WORLD, &status);
        }
    }

    double tmp = *angleP1;
    *angleP1 = *angleP2;
    *angleP2 = max (tmp, *angleP2);

}

void clear(double *angleP2, int myRank, int procCount) {
    if ( (procCount/2)-1 == myRank) {
        *angleP2 = -DBL_MAX;
    }
}

/**
 * Distribute max value into the root of (imaginary) binary tree using pointers to maxPrevAngle(1/2)
 *
 * @param double* angleP1 -> pointer to maxPrevAngle1
 * @param double* angleP2 -> pointer to maxPrevAngle2
 * @param int myRank -> id of current process
 * @param int activeCPUs -> how many processes are active
 * @param int totalCPUs -> total count of available processes
 *
 */
void upSweep(double *angleP1, double *angleP2, int myRank, int activeCPUs, int totalCPUs) {

    bool active = ( (myRank *2 +1) % (totalCPUs / activeCPUs) ) == ( (totalCPUs / activeCPUs) -1);

    if (totalCPUs == activeCPUs) {
        *angleP2 = max(*angleP1, *angleP2);
    }
    else {

        if (active) {
            bool sender = ( (myRank *2 +1) % (totalCPUs / (activeCPUs/2)) ) == ( (totalCPUs / activeCPUs) -1);

            if (sender) {
                MPI_Send(angleP2, 1, MPI_DOUBLE, myRank + (totalCPUs / activeCPUs)/2, MPI_TAG, MPI_COMM_WORLD);
            }
            else {
                double nAngle;
                MPI_Status status;
                MPI_Recv(&nAngle, 1, MPI_DOUBLE, myRank - (totalCPUs / activeCPUs)/2, MPI_TAG, MPI_COMM_WORLD, &status);

                *angleP2 = max(nAngle, *angleP2);
            }
        }
    }

    /* If there is more than two active, and I am active continue */
    if ( (activeCPUs > 2) && (active) ) {
        upSweep(angleP1, angleP2, myRank, activeCPUs/2, totalCPUs);
    }

}

/**
 * Calculates angle between observer point and height point.
 *
 * @param int height -> height of given point
 * @param int idx -> determines first or second point on processor
 * @param int observer -> observer point height
 * @param int procIdx -> index of process = determines distance from observer
 *
 * @returns double -> angle between observer and point
 */
double getAngle(int height, int idx, int observer, int procIdx) {

    if (height == NO_VAL)
        return -DBL_MAX;

    double distance = procIdx * 2;

    if (idx == 2)
        distance += 1;


    return atan(((double)height - (double)observer) / distance);
}

/**
 * Reads input from command line argument and distributes values to other processes
 *
 * @param string inputString -> command line argument containing point heights
 * @param int procCount -> number of available processes for value distribution
 *
 * @returns int -> size of loaded input  (count of height points)
 */
int loadData(string inputString, int procCount) {

    string lNum;
    vector<int> hPoints;

    for ( string::iterator it=inputString.begin(); it!=inputString.end(); ++it) {
        if (*it == ',') {
            hPoints.push_back(stoi(lNum));
            lNum = "";
        }
        else {
            lNum = lNum + *it;
        }
    }

    hPoints.push_back(stoi(lNum)); // push last value after loop was terminated on end of string

    int vecLen = hPoints.size();

    int realCPUs = pow(2, ceil(log2(vecLen)));

    if (realCPUs > 1)
        realCPUs /= 2;

    int activeCPUs = 2 * realCPUs;

    for (int i = 0; i < activeCPUs; i++) {
        if (i < vecLen) {
            MPI_Send(&hPoints[i], 1, MPI_INT, i/2, MPI_TAG, MPI_COMM_WORLD);
        }
        else {
            MPI_Send(&NO_VAL, 1, MPI_INT, i/2, MPI_TAG, MPI_COMM_WORLD);
        }
    }

    for (int i = 0; i < procCount; i++) {
        if (i < realCPUs) {
            MPI_Send(&hPoints[0], 1, MPI_INT, i, MPI_TAG, MPI_COMM_WORLD);
            MPI_Send(&activeCPUs, 1, MPI_INT, i, MPI_TAG, MPI_COMM_WORLD);
        }
        else {
            MPI_Send(&INACTIVE_CPU, 1, MPI_INT, i, MPI_TAG, MPI_COMM_WORLD);
            MPI_Send(&INACTIVE_CPU, 1, MPI_INT, i, MPI_TAG, MPI_COMM_WORLD);
        }
    }

    return vecLen; //how many height poits were loaded

}

int main (int argc, char** argv) {

    int procCount;
    int myRank;
    const int MPI_TAG = 0;
    MPI_Status status;
    int myNum;
    int dataLen;

    //initialize MPI
    MPI_Init(&argc, &argv);

    // get running processes count and process rank
    MPI_Comm_size(MPI_COMM_WORLD, &procCount); // number of running processes
    MPI_Comm_rank(MPI_COMM_WORLD, &myRank);    // process rank

    // only main process loads input
    if (myRank == 0) {
        dataLen = loadData(argv[1], procCount);
    }

    int point1, point2, observerHeight, activeCPUs;

    MPI_Recv(&point1, 1, MPI_INT, 0, MPI_TAG, MPI_COMM_WORLD, &status);
    MPI_Recv(&point2, 1, MPI_INT, 0, MPI_TAG, MPI_COMM_WORLD, &status);

    /* terminate unused processes to keep neat space complexity */
    if ( (point1 == INACTIVE_CPU) && (point2 == INACTIVE_CPU) ) {
        MPI_Finalize();
        return EXIT_SUCCESS;
    }

    MPI_Recv(&observerHeight, 1, MPI_INT, 0, MPI_TAG, MPI_COMM_WORLD, &status);
    MPI_Recv(&activeCPUs, 1, MPI_INT, 0, MPI_TAG, MPI_COMM_WORLD, &status);

     #ifdef BENCHMARK
        MPI_Barrier(MPI_COMM_WORLD);
        if (myRank == 0)
            startTime = high_resolution_clock::now();
    #endif

    double angleP1;

    // set observer place angle to neutral value
    if (myRank == 0) {
        angleP1 = -DBL_MAX;
    }
    else {
        angleP1 = getAngle(point1, 1, observerHeight, myRank);
    }

    double angleP2 = getAngle(point2, 2, observerHeight, myRank);

    double maxPrevAngle1 = angleP1;
    double maxPrevAngle2 = angleP2;

    /* max-prescan begin */
    upSweep(&maxPrevAngle1, &maxPrevAngle2, myRank, activeCPUs, activeCPUs);

    clear(&maxPrevAngle2, myRank, activeCPUs);

    downSweep(&maxPrevAngle1, &maxPrevAngle2, myRank, activeCPUs);
    /* max-prescan end */

    bool isVisibleP1 = angleP1 > maxPrevAngle1 ? true : false;
    bool isVisibleP2 = angleP2 > maxPrevAngle2 ? true : false;

    #ifdef BENCHMARK
        MPI_Barrier(MPI_COMM_WORLD);
        if (myRank == 0)
            endTime = high_resolution_clock::now();
    #endif

    vector<bool> visibilityVector;

    // print visibility if main process
    if (myRank == 0) {
        visibilityVector.push_back(isVisibleP1);
        visibilityVector.push_back(isVisibleP2);

        // start from i=2 because first two values were pushed into vector by main process
        for (int i = 2; i < activeCPUs; i++) {
            bool tmp;
            MPI_Recv(&tmp, 1, MPI_C_BOOL, i/2, MPI_TAG, MPI_COMM_WORLD, &status);
            visibilityVector.push_back(tmp);
        }

        cout << "_";

        for (int i = 1; i < dataLen; i++) {
            cout << "," << (visibilityVector[i] ? "v" : "u");
        }
        cout << endl;

        #ifdef BENCHMARK
            duration<double> runTime = duration_cast<duration<double> >(endTime - startTime);
            cout << "Time: " << runTime.count()*1000000 << " sec" << endl;
        #endif
    }
    // other processes send calculated visibility
    else {
        MPI_Send(&isVisibleP1, 1, MPI_C_BOOL, 0, MPI_TAG, MPI_COMM_WORLD);
        MPI_Send(&isVisibleP2, 1, MPI_C_BOOL, 0, MPI_TAG, MPI_COMM_WORLD);
    }

    MPI_Finalize();
    return EXIT_SUCCESS;
}
