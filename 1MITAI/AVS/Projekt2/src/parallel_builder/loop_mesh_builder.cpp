/**
 * @file    loop_mesh_builder.cpp
 *
 * @author  FULL NAME <xlogin00@stud.fit.vutbr.cz>
 *
 * @brief   Parallel Marching Cubes implementation using OpenMP loops
 *
 * @date    DATE
 **/

#include <iostream>
#include <math.h>
#include <limits>

#include "loop_mesh_builder.h"

LoopMeshBuilder::LoopMeshBuilder(unsigned gridEdgeSize)
    : BaseMeshBuilder(gridEdgeSize, "OpenMP Loop")
{

}

unsigned LoopMeshBuilder::marchCubes(const ParametricScalarField &field)
{
    return 0;
}

float LoopMeshBuilder::evaluateFieldAt(const Vec3_t<float> &pos, const ParametricScalarField &field)
{
    return 0.0f;
}

void LoopMeshBuilder::emitTriangle(const BaseMeshBuilder::Triangle_t &triangle)
{

}
