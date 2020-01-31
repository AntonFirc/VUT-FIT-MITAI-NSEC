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
  const float* input,
  const float* weights,
  float bias
)
{
  float output = bias;
  #pragma omp simd reduction(+:output)
  for (size_t i = 0; i < inputSize; i++) {
    output += input[i] * weights[i];
  }
  return max(output, 0);
}
