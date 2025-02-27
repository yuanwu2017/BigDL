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

package com.intel.analytics.bigdl.dllib.keras.layers

import com.intel.analytics.bigdl.dllib.nn.abstractnn.AbstractModule
import com.intel.analytics.bigdl.dllib.nn.{SpatialMaxPooling, Sequential => TSequential}
import com.intel.analytics.bigdl.dllib.nn.abstractnn.DataFormat
import com.intel.analytics.bigdl.dllib.nn.keras.GlobalPooling2D
import com.intel.analytics.bigdl.dllib.tensor.Tensor
import com.intel.analytics.bigdl.dllib.tensor.TensorNumericMath.TensorNumeric
import com.intel.analytics.bigdl.dllib.utils.Shape
import com.intel.analytics.bigdl.dllib.keras.Net
import com.intel.analytics.bigdl.dllib.keras.layers.utils.KerasUtils

import scala.reflect.ClassTag

/**
 * Applies global max pooling operation for spatial data.
 * The input of this layer should be 4D.
 *
 * When you use this layer as the first layer of a model, you need to provide the argument
 * inputShape (a Single Shape, does not include the batch dimension).
 *
 * @param dimOrdering Format of input data. Please use DataFormat.NCHW (dimOrdering='th')
 *                    or DataFormat.NHWC (dimOrdering='tf'). Default is NCHW.
 * @param inputShape A Single Shape, does not include the batch dimension.
 * @tparam T The numeric type of parameter(e.g. weight, bias). Only support float/double now.
 */
class GlobalMaxPooling2D[T: ClassTag](
    override val dimOrdering: DataFormat = DataFormat.NCHW,
    override val inputShape: Shape = null)(implicit ev: TensorNumeric[T])
  extends GlobalPooling2D[T](dimOrdering, inputShape) with Net {

  override def doBuild(inputShape: Shape): AbstractModule[Tensor[T], Tensor[T], T] = {
    val input = inputShape.toSingle().toArray
    val (dimH, dimW, dimC) = dimOrdering.getHWCDims(4)
    val model = TSequential[T]()
    val layer = SpatialMaxPooling(
      kW = input(dimW -1),
      kH = input(dimH -1),
      dW = input(dimW -1),
      dH = input(dimH -1),
      format = dimOrdering)
    model.add(layer)
    model.add(com.intel.analytics.bigdl.dllib.nn.Squeeze(dimW))
    model.add(com.intel.analytics.bigdl.dllib.nn.Squeeze(dimH))
    model.asInstanceOf[AbstractModule[Tensor[T], Tensor[T], T]]
  }
}

object GlobalMaxPooling2D {
  def apply[@specialized(Float, Double) T: ClassTag](
    dimOrdering: String = "th",
    inputShape: Shape = null)(implicit ev: TensorNumeric[T]): GlobalMaxPooling2D[T] = {
    new GlobalMaxPooling2D[T](KerasUtils.toBigDLFormat(dimOrdering), inputShape)
  }
}
