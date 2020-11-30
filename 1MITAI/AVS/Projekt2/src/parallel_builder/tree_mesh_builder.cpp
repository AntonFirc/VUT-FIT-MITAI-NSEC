/**
 * @file    tree_mesh_builder.cpp
 *
 * @author  Anton Firc <xfirca00@stud.fit.vutbr.cz>
 *
 * @brief   Parallel Marching Cubes implementation using OpenMP task shared(totalTriangles)s + octree early elimination
 *
 * @date    DATE
 **/

#include <iostream>
#include <math.h>
#include <limits>
#include <math.h>

#include "tree_mesh_builder.h"

TreeMeshBuilder::TreeMeshBuilder(unsigned gridEdgeSize)
    : BaseMeshBuilder(gridEdgeSize, "Octree")
{

}

unsigned TreeMeshBuilder::cutCube(Vec3_t<float> &cubeOffset, unsigned aCubeSize, const ParametricScalarField &field) {

    unsigned totalTriangles = 0;

    if (aCubeSize == 1) {
       totalTriangles += buildCube(cubeOffset, field);
    }
    else {

        float condition = field.getIsoLevel() + (sqrt(3) / 2) * (aCubeSize * mGridResolution);

        Vec3_t<float> midPoint(cubeOffset.x+aCubeSize/2, cubeOffset.y+aCubeSize/2, cubeOffset.z+aCubeSize/2);
        Vec3_t<float> midPointNormal(midPoint.x*mGridResolution, midPoint.y*mGridResolution, midPoint.z*mGridResolution);

        if ( evaluateFieldAt(midPointNormal, field) > condition) {
            return 0;
        }
        
        #pragma omp task shared(totalTriangles)
        {
            Vec3_t<float> cube1(cubeOffset.x, cubeOffset.y, cubeOffset.z);
            unsigned tmp1 = cutCube(cube1, aCubeSize/2, field);
            #pragma omp critical
            totalTriangles += tmp1;
        }
        
        #pragma omp task shared(totalTriangles)
        {
            Vec3_t<float> cube2(cubeOffset.x + aCubeSize/2,cubeOffset.y,cubeOffset.z);
            unsigned tmp2 = cutCube(cube2, aCubeSize/2, field);
            #pragma omp critical
            totalTriangles += tmp2;
        }

        #pragma omp task shared(totalTriangles)
        {
            Vec3_t<float> cube3(cubeOffset.x,cubeOffset.y + aCubeSize/2,cubeOffset.z);
            unsigned tmp3 = cutCube(cube3, aCubeSize/2, field);
            #pragma omp critical
            totalTriangles += tmp3;
        }

        #pragma omp task shared(totalTriangles)
        {
            Vec3_t<float> cube4(cubeOffset.x,cubeOffset.y,cubeOffset.z  + aCubeSize/2);
            unsigned tmp4 = cutCube(cube4, aCubeSize/2, field);
            #pragma omp critical
            totalTriangles += tmp4;
        }
        
        #pragma omp task shared(totalTriangles)
        {
            Vec3_t<float> cube5(cubeOffset.x+aCubeSize/2,cubeOffset.y+aCubeSize/2,cubeOffset.z);
            unsigned tmp5 = cutCube(cube5, aCubeSize/2, field);
            #pragma omp critical
            totalTriangles += tmp5;
        }

        #pragma omp task shared(totalTriangles)
        {
            Vec3_t<float> cube6(cubeOffset.x+aCubeSize/2,cubeOffset.y,cubeOffset.z+aCubeSize/2);
            unsigned tmp6 = cutCube(cube6, aCubeSize/2, field); 
            #pragma omp critical
            totalTriangles += tmp6;
        } 

        #pragma omp task shared(totalTriangles)
        {
            Vec3_t<float> cube7(cubeOffset.x,cubeOffset.y+aCubeSize/2,cubeOffset.z+aCubeSize/2);
            unsigned tmp7 = cutCube(cube7, aCubeSize/2, field);
            #pragma omp critical
            totalTriangles += tmp7;
        }
        
        #pragma omp task shared(totalTriangles)
        {
            Vec3_t<float> cube8(cubeOffset.x+aCubeSize/2,cubeOffset.y+aCubeSize/2,cubeOffset.z+aCubeSize/2);
            unsigned tmp8 = cutCube(cube8, aCubeSize/2, field);
            #pragma omp critical
            totalTriangles += tmp8;
        }
    }

    #pragma omp taskwait
    return totalTriangles;
}

unsigned TreeMeshBuilder::marchCubes(const ParametricScalarField &field)
{

    Vec3_t<float> initialPosition(0,0,0);
    unsigned totalTriangles;
    #pragma omp parallel
    #pragma omp single
    totalTriangles = cutCube(initialPosition, mGridSize, field);
    
    return totalTriangles;
    
}

float TreeMeshBuilder::evaluateFieldAt(const Vec3_t<float> &pos, const ParametricScalarField &field)
{
    // NOTE: This method is called from "buildCube(...)"!

    // 1. Store pointer to and number of 3D points in the field
    //    (to avoid "data()" and "size()" call in the loop).
    const Vec3_t<float> *pPoints = field.getPoints().data();
    const unsigned count = unsigned(field.getPoints().size());

    float value = std::numeric_limits<float>::max();

    // 2. Find minimum square distance from points "pos" to any point in the
    //    field.
    for(unsigned i = 0; i < count; ++i)
    {
        float distanceSquared  = (pos.x - pPoints[i].x) * (pos.x - pPoints[i].x);
        distanceSquared       += (pos.y - pPoints[i].y) * (pos.y - pPoints[i].y);
        distanceSquared       += (pos.z - pPoints[i].z) * (pos.z - pPoints[i].z);

        // Comparing squares instead of real distance to avoid unnecessary
        // "sqrt"s in the loop.
        value = std::min(value, distanceSquared);
    }

    // 3. Finally take square root of the minimal square distance to get the real distance
    return sqrt(value);
}

void TreeMeshBuilder::emitTriangle(const BaseMeshBuilder::Triangle_t &triangle)
{
    #pragma omp critical
    mTriangles.push_back(triangle);
}
