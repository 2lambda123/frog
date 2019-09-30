/*
 * Copyright 2018 the original author or authors. 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.frog.brain;

/**
 * Photon has direction and strength
 * 
 * 用光子来代表信息传递的载体，这是一个矢量，具有方向和能量，能量可以为负值，光子的x,y,z值表示它在每个测试step中将移动多少个脑单位距离
 * 光子永远在移动，直到被吸收转化为Cell的能量贮存或出界
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class Photon {
	public float x;// 用 x,y,z长度来表示矢量的方向，便于今后计算
	public float y;
	public float z;
	public float energy;

	public Photon() { // 缺省构造器
	}

	public Photon(float x, float y, float z, float energy) { // 缺省构造器
		this.x = x;
		this.y = y;
		this.z = z;
		this.energy = energy;
	}
}