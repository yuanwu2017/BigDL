/*
 * Copyright 2016 The BigDL Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.analytics.bigdl.dllib.nn.keras

import com.intel.analytics.bigdl.dllib.nn._
import com.intel.analytics.bigdl.dllib.nn.{Sequential => TSequential}
import com.intel.analytics.bigdl.dllib.nn.abstractnn.{AbstractModule, DataFormat}
import com.intel.analytics.bigdl.dllib.tensor.Tensor
import com.intel.analytics.bigdl.dllib.tensor.TensorNumericMath.TensorNumeric
import com.intel.analytics.bigdl.dllib.utils.Shape

import scala.reflect.ClassTag

/**
 * Applies average pooling operation for temporal data.
 * The input of this layer should be 3D.
 *
 * When you use this layer as the first layer of a model, you need to provide the argument
 * inputShape (a Single Shape, does not include the batch dimension).
 *
 * @param poolLength Size of the region to which average pooling is applied. Integer. Default is 2.
 * @param stride Factor by which to downscale. Positive integer, or -1. 2 will halve the input.
 *               If -1, it will default to poolLength. Default is -1, and in this case it will
 *               be equal to poolSize.
 * @param borderMode Either 'valid' or 'same'. Default is 'valid'.
 * @tparam T Numeric type of parameter(e.g. weight, bias). Only support float/double now.
 */
class AveragePooling1D[T: ClassTag](
   poolLength: Int = 2,
   stride: Int = -1,
   borderMode: String = "valid",
   inputShape: Shape = null)(implicit ev: TensorNumeric[T])
  extends Pooling1D[T](poolLength, stride, borderMode, inputShape) {

  override def doBuild(inputShape: Shape): AbstractModule[Tensor[T], Tensor[T], T] = {
    val input = inputShape.toSingle().toArray
    val pads = KerasUtils.getPadsFromBorderMode(borderMode)
    val model = TSequential[T]()
    model.add(com.intel.analytics.bigdl.dllib.nn.Reshape(Array(input(1), 1, input(2)), Some(true)))
    val layer = SpatialAveragePooling(
      kW = 1,
      kH = poolLength,
      dW = 1,
      dH = strideValue,
      padW = pads._2,
      padH = pads._1,
      countIncludePad = false,
      format = DataFormat.NHWC)
    model.add(layer)
    model.add(Squeeze(3))
    model.asInstanceOf[AbstractModule[Tensor[T], Tensor[T], T]]
  }
}

object AveragePooling1D {
  def apply[@specialized(Float, Double) T: ClassTag](
    poolLength: Int = 2,
    stride: Int = -1,
    borderMode: String = "valid",
    inputShape: Shape = null)(implicit ev: TensorNumeric[T]): AveragePooling1D[T] = {
    new AveragePooling1D[T](poolLength, stride, borderMode, inputShape)
  }
}
