#Simple python3 script to visualize input images.
#Usage: python3 showNumber.py dataset.h5 imageNumber

import sys
import h5py
import numpy as np
import matplotlib.pyplot as plt

if len(sys.argv) != 3:
    print("Expected two arguments. HDF5 file containing input images and image number.")
    sys.exit(1)

num_inp = int(sys.argv[2])
filename = sys.argv[1]

f = h5py.File(filename,'r')

image_count = np.array(f['image_count'])[0].astype(np.int64)

if num_inp >= image_count:
    print("Dataset does not contain the specified image. Requested image number", num_inp, "from dataset with", image_count, "images." )
    sys.exit(1)

inp = np.array(f['x_test'])
plt.imshow(inp[num_inp,:,:])
plt.show()
