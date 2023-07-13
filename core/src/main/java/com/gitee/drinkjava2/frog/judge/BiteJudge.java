package com.gitee.drinkjava2.frog.judge;

import com.gitee.drinkjava2.frog.objects.EnvObject;

/**
 * BiteJudge，the simplest image recognition
 * 
 * ======================================
 * 暂停!!! BiteJudge还是太复杂了，打算删除它，改成一个Eye类仅用来在视网膜上投影随机序号的条形码，然后咬、、苦、甜感觉、加减分都在Cell细胞的Active里来做,也就是说逻辑不集中在一起，而是分散到各个细胞里
 * 然后视觉信号也简化为单维的线条性号，从而可以用阴阳2叉分裂算法快速验证思路
 * =======================================
 * 
 * 以下作废：
 * 
 * BiteJudge用来进行模式识别训练并检查结果是否正确，这是个临时类。它主要有 随机、饿、视、咬、苦、甜、忆类细胞，可以实现最简单的模式识别，即根据图像来决定咬还是不咬。
 * 最初打算用CharJudge即字符的图像来做模式识别，但发现复杂了一点。这个BiteJudge是精简过后的，能够实现模式识别的最简脑模型，它去除了CharJudge的声音输入细胞和SPEAK说话细胞，用苦、甜、咬三种细胞代替。
 * 
 * 基本思路是
 * 1.随机在视网膜EYE上生成一个食物图像(就用食物序号的ASCII二进制码图)，如果这时碰巧咬细胞激活，就会根据食物本身是否有毒，激活frog的苦或者甜感觉细胞, 并同时增减frog的能量
 * 2.frog要能进化出模式识别功能，即原来的信号顺序是图-咬(随机)-甜， 最后演变为图-甜-咬， 即看到图来预测是要咬还是不咬，这就是最简单模式识别了。
 * 
 * 与CharJudge相比，可以看到训练和提问信号没有了，咬动作直接由图像驱动，至于这个关联是如何生成的，是否正确，并不在这个类里实现。
 *  
 * 这个类只是用来模仿外界和内部信号的产生和输入输出, 以及判断正误后加减能量以淘汰生物，脑的结构（即逻辑功能）的生成以及信号的传输不在这里，而是由8叉树或4叉树算法配合遗传算法来生成，见Cells.java
 * 
 */
public class BiteJudge extends EnvObject {

}
