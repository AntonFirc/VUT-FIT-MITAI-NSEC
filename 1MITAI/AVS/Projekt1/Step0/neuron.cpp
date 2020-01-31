/*
 * Architektury výpočetních systémů (AVS 2019)
 * Projekt c. 1 (ANN)
 * Login: xfirca00
 */

#include <cstdlib>
#include "neuron.h"
#include <algorithm>
#include <stdio.h>

float max(float a, float b) {
  return a > b ? a : b;
}

float evalNeuron(
  size_t inputSize,
  size_t neuronCount,
  const float* input,
  const float* weights,
  float bias,
  size_t neuronId
)
{
  float output = bias;
  for (int i = 0; i < inputSize; i++) {
    output += input[i] * weights[neuronId + i*neuronCount];
  }
  return max(output, 0);
  //TODO: Step0 - Fill in the implementation, all the required arguments are passed.
  //              If you don't use them all you are doing something wrong!
  //return 0.0f;
}
