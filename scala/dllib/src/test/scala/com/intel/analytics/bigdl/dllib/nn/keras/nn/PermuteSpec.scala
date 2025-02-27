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

package com.intel.analytics.bigdl.keras.nn

import com.intel.analytics.bigdl.keras.KerasBaseSpec
import com.intel.analytics.bigdl.dllib.nn.abstractnn.AbstractModule
import com.intel.analytics.bigdl.dllib.nn.keras.{Permute, Sequential => KSequential}
import com.intel.analytics.bigdl.dllib.tensor.Tensor
import com.intel.analytics.bigdl.dllib.utils.Shape
import com.intel.analytics.bigdl.dllib.utils.serializer.ModuleSerializationTest

import scala.util.Random

class PermuteSpec extends KerasBaseSpec {

  "Permute" should "be the same as Keras" in {
    val kerasCode =
      """
        |input_tensor = Input(shape=[3, 4, 5, 6])
        |input = np.random.random([2, 3, 4, 5, 6])
        |output_tensor = Permute((3, 1, 4, 2))(input_tensor)
        |model = Model(input=input_tensor, output=output_tensor)
      """.stripMargin
    val seq = KSequential[Float]()
    val layer = Permute[Float](Array(3, 1, 4, 2), inputShape = Shape(3, 4, 5, 6))
    seq.add(layer)
    seq.getOutputShape().toSingle().toArray should be (Array(-1, 5, 3, 6, 4))
    checkOutputAndGrad(seq.asInstanceOf[AbstractModule[Tensor[Float], Tensor[Float], Float]],
      kerasCode)
  }

}
class PermuteSerialTest extends ModuleSerializationTest {
  override def test(): Unit = {
    val layer = Permute[Float](Array(3, 1, 4, 2), inputShape = Shape(3, 4, 5, 6))
    layer.build(Shape(2, 3, 4, 5, 6))
    val input = Tensor[Float](2, 3, 4, 5, 6).apply1(_ => Random.nextFloat())
    runSerializationTest(layer, input)
  }
}
