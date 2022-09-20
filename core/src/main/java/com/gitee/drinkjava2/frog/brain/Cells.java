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
package com.gitee.drinkjava2.frog.brain;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.util.Logger;

/**
 * Cells代表不同的脑细胞参数，对应每个参数，用8叉树算法生成不同的细胞。
 * 每个脑细胞用一个long来存储，所以最多允许64个基因位, 有时一个参数由多个基因位决定。每个基因位都由一个单独的阴阳8叉树控制，多个基因就组成了一个8叉树阵列
 * 基因+分裂算法=结构
 * 基因+分裂算法+遗传算法=结构的进化
 * 这个类里定义每个基因位的掩码, 脑结构的所有参数，都要用基因来控制。开始时可以有常量、魔数，但以后都放到基因里去自动进化。
 * 
 * 
 * @author Yong Zhu
 * @since 10.0
 */
@SuppressWarnings("all")
public class Cells {
    public static int GENE_NUMBERS = 0;
    private static int zeros = 0;

    public static long EXIST = mask(1); // 细胞存在否,1为存在,0为不存在
    public static long EXIST0 = zeros; //0后缀的同名变量，表示当前掩码低位有多少个0，下同
    public static long ACTIVED = mask(1); //如此点为1，则此细胞位置处会始终产生能量
    public static long ACTIVED0 = zeros;
    public static long MOVE_UP = mask(2); //如此点为1，则此细胞如有能量，青蛙向上移动
    public static long MOVE_UP0 = zeros;
    public static long MOVE_DOWN = mask(2); //如此点为1，则此细胞如有能量，青蛙向下移动
    public static long MOVE_DOWN0 = zeros;
    public static long MOVE_LEFT = mask(2); //如此点为1，则此细胞如有能量，青蛙向左移动
    public static long MOVE_LEFT0 = zeros;
    public static long MOVE_RIGHT = mask(2); //如此点为1，则此细胞如有能量，青蛙向右移动
    public static long MOVE_RIGTH0 = zeros;
    public static long POSITIVE = mask(1);//细胞信号,1为正信号,0为负(抑制)信号
    public static long POSITIVE0 = zeros;
    public static long ZTX = mask(2);//axon x offset, 轴突x方向 (2个0), 轴突方向由x,y,z三个方向的参数组合决定
    public static long ZTX0 = zeros;
    public static long ZTY = mask(2); //轴突y方向 (4个0)
    public static long ZTY0 = zeros;
    public static long ZTZ = mask(2); //轴突z方向 (6个0)
    public static long ZTZ0 = zeros;
    public static long ZT_LONG = mask(3); //轴突长度 (8个0)
    public static long ZT_LONG0 = zeros;
    public static long ST_LONG = mask(2); //dendrite length, 树突长度 (11个0)
    public static long ST_LONG0 = zeros;


    public static long mask(int n) { //返回基因掩码，高位由n个1组成，低位是当前GENE_NUMBERS个0，这个方法执行后GENE_NUMBERS会加n
        String one = "";
        String zero = "";
        for (int i = 1; i <= n; i++)
            one += "1";
        for (int i = 1; i <= GENE_NUMBERS; i++)
            zero += "0";
        zeros = GENE_NUMBERS;
        GENE_NUMBERS += n;
        if (GENE_NUMBERS >= 64) {//
            System.out.println("目前基因位数不能超过64");
            System.exit(-1);
        }
        return Long.parseLong(one + zero, 2); //将"111000"这种字符串转换为长整
    }

    public static void active(Animal a) {//这个方法的功能是根据细胞的参数，在细胞间传输能量（即信息的载体)
        for (int z = Env.BRAIN_CUBE_SIZE - 1; z >= 0; z--)
            for (int y = Env.BRAIN_CUBE_SIZE - 1; y >= 0; y--)
                for (int x = Env.BRAIN_CUBE_SIZE - 1; x >= 0; x--) {
                    long cell = a.cells[x][y][z];

                    if ((cell & EXIST) == 0) //如细胞不存在，
                        continue;

                    if ((cell & ACTIVED) > 0) //如有这个基因，则当前细胞始终产生能量
                        a.energys[x][y][z] = 10;

                    float e = a.energys[x][y][z];

                    if (e > 0) { //如有轴突基因，则当前细胞如存在能量，会输送到轴突端点处
//                        int x_ = (int) ((cell & ZTX) >> ZTX0) - 2;//让x_位于-2,-1,1,2这个个数值中，表示x方向的坐标方向偏移，下同
//                        if (x_ >= 0)
//                            x_++;
//                        int y_ = (int) ((cell & ZTY) >> ZTY0) - 2;
//                        if (y_ >= 0)
//                            y_++;
//                        int z_ = (int) ((cell & ZTZ) >> ZTZ0) - 2;
//                        if (y_ >= 0)
//                            y_++;
//                        int zt_long = (int) ((cell & ZT_LONG) >> ZT_LONG0) + 1; //轴突长度, 大小为1~8
//                        int xx = x + x_ * zt_long;
//                        int yy = y + y_ * zt_long;
//                        int zz = z + z_ * zt_long;
//                        if (Env.insideBrain(xx, yy, zz)) {
//                            if (a.energys[xx][yy][zz] < 5)
//                                a.energys[xx][yy][zz]++;
//                            if (a.energys[x][y][z] > 1)
//                                a.energys[x][y][z]--;
//                        }
                    }

                    if (e > 1 && z==0) { //如当前细胞有移动基因，且有能量，则青蛙移动
                        if ((cell & MOVE_UP) > 0) {
                            a.y++;
                            a.energys[x][y][z] = 0;
                        }
                        if ((cell & MOVE_DOWN) > 0) {
                            a.y--;
                            a.energys[x][y][z] = 0;
                        }
                        if ((cell & MOVE_LEFT) > 0) {
                            a.x--;
                            a.energys[x][y][z] = 0;
                        }
                        if ((cell & MOVE_RIGHT) > 0) {
                            a.x++;
                            a.energys[x][y][z] = 0;
                        }
                    }
                }
    }

}
