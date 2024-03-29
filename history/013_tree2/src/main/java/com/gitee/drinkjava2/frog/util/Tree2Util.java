/* Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.gitee.drinkjava2.frog.util;

import java.util.List;

import com.gitee.drinkjava2.frog.Env;

/**
 * Tree2Util used to store a pre-order Traversal 2tree array to speed 
 * 
 * 这里缓存着一个前序排列的2叉树用来加快速度
 * 这里的2叉树和4叉树的原理类似，只不过4叉树生成2维平面形状， 2叉树用来生成一维线状，对于线状生成速度更快
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Tree2Util {

    //EIGHT_TREE store a pre-order Traversal tree array  
    public static final int NODE_QTY = calculateNodeSize(Env.BRAIN_CUBE_SIZE);

    public static int[][] TREE2 = new int[NODE_QTY][2]; //2叉数用数组表示，第一维是深度树的行号，第二维是一个整数数组,内容是深度树表示的2叉树细胞的size, x值

    public static byte[] keep = new byte[NODE_QTY]; //这里临时记录树的敲除记录，大于0的值表示要keep, 小于等于0表示要敲除

    private static byte[] KEEP0 = new byte[NODE_QTY]; //这里保存初值为0的数组常量，可以用System.arraycopy(KEEP, 0, keep, 0, NODE_QTY)快速清空enable数组

    public static int keepNodeQTY = NODE_QTY; //这里临时记录需keep的节点总数，好用来继续敲除，初始值是全部节点

    private static int index = 0;
    static {
        tree2Split(0, Env.BRAIN_CUBE_SIZE);
    }

    static int calculateNodeSize(int n) {//计算2叉树全展开的总节点数
        if (n == 1)
            return 1;
        return n  + calculateNodeSize(n / 2);
    }

    //if cube can split, then split it to 8 small cubes
    private static void tree2Split(int x, int size) {//如正方形可分裂，就继续递归分裂成4个
        TREE2[index++] = new int[]{size, x}; //这里size类似于深度树中的level，只不过是size从大到小，level是从小到大，原理一样
        if (size == 1)
            return;
        int half = size / 2;//每个细胞可以分裂成2个size为原来1/2的小细胞
        tree2Split(x, half);
        tree2Split(x+half, half);
    }

    public static void knockNodesByGene(List<Integer> gene) {//根据基因，把要敲除的2叉树节点作敲除或保留标记 
        System.arraycopy(KEEP0, 0, keep, 0, NODE_QTY);//清空keep数组
        keepNodeQTY = 0;
        for (int g : gene) {//g基因，用带符号的2叉数的行号表示，负数表示阴节点要敲除，正数表示是阳节点要保留
            int gLine = Math.abs(g); //基因对应节点的行号
            int size = Tree2Util.TREE2[gLine][0]; //size是基因对应节点的细胞正方形边长
            for (int line = gLine; line < Tree2Util.NODE_QTY; line++) {//从这个g节点开始，往下找节点
                if (line > gLine && Tree2Util.TREE2[line][0] >= size) //如果除了第一个节点外，边长大于等于size，说明节点不是g的子节点，退出
                    break;
                else {//否则就是g的子节点
                    if (g < 0) { //g是阴节点 
                        if (Tree2Util.keep[line] == 1) //如果是1，表示这个节点将从保留状态转为删除状态
                            keepNodeQTY--;
                        Tree2Util.keep[line] = 0;
                    } else if (g > 0) { //g是阳节点
                        if (Tree2Util.keep[line] == 0) //如果是0，表示这个节点将从删除状态转为保留状态
                            keepNodeQTY++;
                        Tree2Util.keep[line] = 1;
                    }
                }
            }
        }
    }

}
