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
package com.github.drinkjava2.frog;

import java.awt.Graphics;
import java.awt.Image;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.egg.Egg;

/**
 * Frog = organs + brain cells
 * 
 * 青蛙由器官组成，器官中的Group类会生成各种脑细胞
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Frog {

	/** brain cells */
	public List<Cell> cells = new ArrayList<>();

	/** organs */
	public List<Organ> organs = new ArrayList<>();

	public int x; // frog在Env中的x坐标
	public int y; // frog在Env中的y坐标
	public long energy = 10000; // 能量为0则死掉
	public boolean alive = true; // 设为false表示青蛙死掉了，将不参与计算和显示，以节省时间

	static Image frogImg;
	static {
		try {
			frogImg = ImageIO.read(new FileInputStream(Application.CLASSPATH + "frog.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Frog(int x, int y, Egg egg) {
		this.x = x;
		this.y = y;
		for (Organ org : egg.organs)
			for (Organ newOrgan : org.vary()) {
				organs.add(newOrgan);
				newOrgan.init(this);// 每个新器官初始化，如果是Group类，它们会生成许多脑细胞
			}
	}

	public boolean activeOrgan(Env v) {// 每一个步长都会调用Organ的active方法
		for (Organ o : organs)
			o.active(this);
		return alive;
	}

	public void show(Graphics g) {// 显示青蛙的图象
		if (!alive)
			return;
		g.drawImage(frogImg, x - 8, y - 8, 16, 16, null);
	}

}
