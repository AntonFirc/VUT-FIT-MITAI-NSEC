#Simple python3 script to visualize input images.
#Usage: python3 showNumber.py dataset.h5 imageNumber

import sys
import h5py
import numpy as np
import matplotlib.pyplot as plt

if len(sys.argv) != 3:
    print("Expected two arguments. Output and reference output file.")
    sys.exit(1)

filename = sys.argv[1]
ref_filename = sys.argv[2]

f = h5py.File(filename,'r')
ref_f = h5py.File(ref_filename,'r')

out = np.array(f['output_data'])
out_ref = np.array(ref_f['output_data'])

if out.shape != out_ref.shape:
    print("The files do not contain the same number of outputs.")
    print("The output size:", out.shape[0])
    print("The reference size:", out_ref.shape[0])

    sys.exit(1)

ref_value = np.copy(out_ref)
ref_value[ref_value == 0.0] = 1.0

error = (out_ref - out)/ref_value

maximal_error = np.amax(error)

print("Maximal error between the output and the reference is", maximal_error)
if maximal_error < 10**-6 :
    print("OK:Output seems to match the reference")
    sys.exit(0)

print("Failure:Output does not match the reference")
maximal_error = np.amax(error, axis=1)
print(maximal_error.shape)

for i in range(0,5):
    print("Image", i)
    print("Expected:", end="")
    for j in range(0,10):
        print(out_ref[i,j], end = " ")
    print("\nGot:", end="")
    for j in range(0,10):
        print(out[i,j], end=" ")
    print("\nMaximal error:", maximal_error[i], "\n")

sys.exit(1)
