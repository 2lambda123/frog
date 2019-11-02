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

import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.Room;
import com.github.drinkjava2.frog.egg.Egg;
import com.github.drinkjava2.frog.objects.Material;

/**
 * Frog = organs + rooms <br/>
 * rooms = brain cells + photons <br/>
 * organs = cell parameters + cell actions
 * 
 * 青蛙脑由器官播种出的细胞组成，器官Organ会播种出各种脑细胞填充在一个rooms三维数组代表的空间中，每个room里可以存在多个脑细胞和光子，光子是信息的载体，永远不停留。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Frog {
	/** brain rooms */
	public Room[][][] rooms;// 一开始不要初始化，只在调用getRoom方法时才初始化相关维以节约内存

	/** organs */
	public List<Organ> organs = new ArrayList<>();

	public int x; // frog在Env中的x坐标
	public int y; // frog在Env中的y坐标
	public long energy = 10000000; // 青蛙的能量为0则死掉
	public boolean alive = true; // 设为false表示青蛙死掉了，将不参与计算和显示，以节省时间
	public int ateFood = 0;

	static Image frogImg;
	static {
		try {
			frogImg = ImageIO.read(new FileInputStream(Application.CLASSPATH + "frog.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Frog(int x, int y, Egg egg) {
		this.x = x; // x, y 是虑拟环境的坐标
		this.y = y;
		for (Organ org : egg.organs)
			organs.add(org);
	}

	public void initFrog() {// 仅在测试之前调用这个方法初始化frog以节约内存，测试完成后要清空units释放内存
		try {
			rooms = new Room[Env.FROG_BRAIN_XSIZE][][]; // 为了节约内存，先只初始化三维数组的x维，另两维用到时再分配
		} catch (OutOfMemoryError e) {
			System.out.println("OutOfMemoryError found for frog, force it die.");
			this.alive = false;
			return;
		}
		for (Organ org : organs)
			org.init(this);
	}

	/** Find a organ in frog by organ's name */
	@SuppressWarnings("unchecked")
	public <T extends Organ> T findOrganByName(String organName) {// 根据器官名寻找器官，但不是每个器官都有名字
		for (Organ o : organs)
			if (o.organName != null && organName.equalsIgnoreCase(o.organName))
				return (T) o;
		return null;
	}

	/** Set with given activeValue */
	public void setCuboidVales(Cuboid o, float active) {// 激活长方体区域内的所有脑区
		if (!alive)
			return;
		for (int x = o.x; x < o.x + o.xe; x++)
			if (rooms[x] != null)
				for (int y = o.y; y < o.y + o.ye; y++)
					if (rooms[x][y] != null)
						for (int z = o.z; z < o.z + o.ze; z++)
							if (rooms[x][y][z] != null)
								getOrCreateRoom(x, y, z).setActive(active);
	}

	/** Calculate organ activity by add all organ rooms' active value together */
	public float getCuboidActiveTotalValue(Cuboid o) {// 遍历长方体区域所在room，将它们的激活值汇总返回
		float activity = 0;
		for (int x = o.x; x < o.x + o.xe; x++)
			for (int y = o.y; y < o.y + o.ye; y++)
				for (int z = o.z; z < o.z + o.ze; z++)
					activity += this.getOrCreateRoom(x, y, z).getActive();
		return activity;
	}

	public boolean active(Env v) {// 这个active方法在每一步循环都会被调用，是脑思考的最小帧
		// 如果能量小于0、出界、与非食物的点重合则判死
		if (!alive || energy < 0 || Env.outsideEnv(x, y) || Env.bricks[x][y] >= Material.KILLFROG) {
			energy -= 100; // 死掉的青蛙也要消耗能量，确保淘汰出局
			alive = false;
			return false;
		}
		energy -= 20;
		for (Organ o : organs)
			o.active(this); // 调用每个器官的active方法， 通常只用于执行器官的外界信息输入、动作输出，脑细胞的遍历不是在这一步

		// 这里是最关键的脑细胞主循环，脑细胞负责捕获和发送光子，光子则沿它的矢量方向每次自动走一格，如果下一格是真空(即room未初始化）会继续走下去并衰减直到为0(为减少运算)
		for (int i = 0; i < Env.FROG_BRAIN_XSIZE; i++)
			if (rooms[i] != null)
				for (int j = 0; j < Env.FROG_BRAIN_YSIZE; j++)
					if (rooms[i][j] != null)
						for (int k = 0; k < Env.FROG_BRAIN_ZSIZE; k++) {
							Room room = rooms[i][j][k];
							if (room != null)
								room.execute(this, i, j, k);// 调用room的方法来进行这个运算
						}
		return alive;
	}

	public void show(Graphics g) {// 显示青蛙的图象
		if (!alive)
			return;
		g.drawImage(frogImg, x - 8, y - 8, 16, 16, null);
	}

	/** Check if room exist */
	public Room getRoom(int x, int y, int z) {// 返回指定脑ssf坐标的room ，如果不存在，返回null
		if (rooms[x] == null || rooms[x][y] == null)
			return null;
		return rooms[x][y][z];
	}

	/** Get a room in position (x,y,z), if not exist, create a new one */
	public Room getOrCreateRoom(int x, int y, int z) {// 获取指定坐标的Room，如果为空，则在指定位置新建Room
		if (rooms[x] == null)
			rooms[x] = new Room[Env.FROG_BRAIN_YSIZE][];
		if (rooms[x][y] == null)
			rooms[x][y] = new Room[Env.FROG_BRAIN_ZSIZE];
		if (rooms[x][y][z] == null) {
			Room unit = new Room();
			rooms[x][y][z] = unit;
			return unit;
		} else
			return rooms[x][y][z];
	}

}
