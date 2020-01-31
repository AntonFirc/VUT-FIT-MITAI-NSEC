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
  for (size_t i = 0; i < inputSize; i++) {
    output += input[i] * weights[neuronId + i*neuronCount];
  }
  return max(output, 0.0f);
}
