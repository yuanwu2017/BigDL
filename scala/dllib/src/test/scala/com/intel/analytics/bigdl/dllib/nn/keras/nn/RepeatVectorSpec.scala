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
import com.intel.analytics.bigdl.dllib.nn.keras.{RepeatVector, Sequential => KSequential}
import com.intel.analytics.bigdl.dllib.tensor.Tensor
import com.intel.analytics.bigdl.dllib.utils.Shape
import com.intel.analytics.bigdl.dllib.utils.serializer.ModuleSerializationTest

import scala.util.Random

class RepeatVectorSpec extends KerasBaseSpec {

  "RepeatVector" should "be the same as Keras" in {
    val kerasCode =
      """
        |input_tensor = Input(shape=[12])
        |input = np.random.random([2, 12])
        |output_tensor = RepeatVector(4)(input_tensor)
        |model = Model(input=input_tensor, output=output_tensor)
      """.stripMargin
    val seq = KSequential[Float]()
    val layer = RepeatVector[Float](4, inputShape = Shape(12))
    seq.add(layer)
    seq.getOutputShape().toSingle().toArray should be (Array(-1, 4, 12))
    checkOutputAndGrad(seq.asInstanceOf[AbstractModule[Tensor[Float], Tensor[Float], Float]],
      kerasCode)
  }

}

class RepeatVectorSerialTest extends ModuleSerializationTest {
  override def test(): Unit = {
    val layer = RepeatVector[Float](4, inputShape = Shape(12))
    layer.build(Shape(2, 12))
    val input = Tensor[Float](2, 12).apply1(_ => Random.nextFloat())
    runSerializationTest(layer, input)
  }
}
