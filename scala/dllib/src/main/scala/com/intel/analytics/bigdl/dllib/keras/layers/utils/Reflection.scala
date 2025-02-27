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

package com.intel.analytics.bigdl.dllib.keras.layers.utils

import com.intel.analytics.bigdl.dllib.nn.{Graph, MklInt8Convertible}
import com.intel.analytics.bigdl.dllib.nn.Graph.ModuleNode
import com.intel.analytics.bigdl.dllib.nn.abstractnn.{AbstractModule, Activity}
import com.intel.analytics.bigdl.dllib.nn.keras.KerasLayer
import com.intel.analytics.bigdl.dllib.optim.SGD
import com.intel.analytics.bigdl.dllib.utils._
import com.intel.analytics.bigdl.dllib.keras.optimizers.{Adam, AdamWeightDecay}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

object KerasLayerRef {
  def apply[T: ClassTag](instance: KerasLayer[_, _, T]): KerasLayerRef[T] = {
    new KerasLayerRef(instance)
  }
}

class KerasLayerRef[T: ClassTag](instance: KerasLayer[_, _, T]) {

  def excludeInvalidLayers[T: ClassTag]
  (modules : Seq[AbstractModule[_, _, T]]): Unit = {
    KerasUtils.invokeMethod(instance, "excludeInvalidLayers", modules, ClassTag(this.getClass))
  }

  def setInputShape(value: Shape): Unit = {
    KerasUtils.invokeMethod(instance, "_inputShapeValue_$eq", value)
  }

  def setOutShape(value: Shape): Unit = {
    KerasUtils.invokeMethod(instance, "_outputShapeValue_$eq", value)
  }

  def checkWithCurrentInputShape(calcInputShape: Shape): Unit = {
    KerasUtils.invokeMethod(instance, "checkWithCurrentInputShape", calcInputShape)
  }

  def validateInput[T: ClassTag](modules : Seq[AbstractModule[_, _, T]]): Unit = {
    KerasUtils.invokeMethod(instance, "validateInput", modules, ClassTag(this.getClass))
  }

  def checkDuplicate(
      record: mutable.HashSet[Int] = mutable.HashSet()
  ): Unit = {
    KerasUtils.invokeMethod(instance, "checkDuplicate", record)
  }
}

class AbstractModuleRef[T: ClassTag](instance: AbstractModule[Activity, Activity, T]) {

  def build(inputShape: Shape): Shape = {
    KerasUtils.invokeMethod(instance, "build", inputShape).asInstanceOf[Shape]
  }
}

class GraphRef[T: ClassTag](instance: Graph[T]) {
  def getOutputs(): Seq[ModuleNode[T]] = {
    KerasUtils.invokeMethod(instance, "outputs").asInstanceOf[Seq[ModuleNode[T]]]  // !!!!
  }
}

object EngineRef {
  def getCoreNumber(): Int = {
    KerasUtils.invokeMethod(Engine, "coreNumber").asInstanceOf[Int]
  }

  def getNodeNumber(): Int = {
    KerasUtils.invokeMethod(Engine, "nodeNumber").asInstanceOf[Int]
  }

  def getDefaultThreadPool(): ThreadPool = {
    KerasUtils.invokeMethod(Engine, "default").asInstanceOf[ThreadPool]
  }

  def getEngineType(): EngineType = {
    KerasUtils.invokeMethod(Engine, "getEngineType").asInstanceOf[EngineType]
  }

  def getOptimizerVersion(): OptimizerVersion = {
    KerasUtils.invokeMethod(Engine, "getOptimizerVersion").asInstanceOf[OptimizerVersion]
  }

  def setOptimizerVersion(optimizerVersion : OptimizerVersion): Unit = {
    KerasUtils.invokeMethod(Engine, "setOptimizerVersion",
      optimizerVersion).asInstanceOf[OptimizerVersion]
  }

  def setCoreNumber(num: Int): Unit = {
    val field = Engine.getClass.getDeclaredField("physicalCoreNumber")
    field.setAccessible(true)
    field.setInt(Engine, num)
  }
}

object SGDRef {
  def getstate[T: ClassTag](instance: Adam[T]): Table = {
    KerasUtils.invokeMethod(instance, "state").asInstanceOf[Table]
  }

  def getstate[T: ClassTag](instance: AdamWeightDecay[T]): Table = {
    KerasUtils.invokeMethod(instance, "state").asInstanceOf[Table]
  }

  def getstate[T](instance: SGD[T]): Table = {
    KerasUtils.invokeMethod(instance, "state").asInstanceOf[Table]
  }
}

object MklInt8ConvertibleRef {
  def getWeightScalesBuffer(instance: MklInt8Convertible): ArrayBuffer[Array[Float]] = {
    KerasUtils.invokeMethod(instance, "weightScalesBuffer").asInstanceOf[ArrayBuffer[Array[Float]]]
  }
}
