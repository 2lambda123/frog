package com.gitee.drinkjava2.frog.objects;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Eye的作用只有一个，就是把一个随机序号的二进制条码投射到视网膜细胞上（暂定于x=0的YZ平面上）
 * 
 * 当前目标是实现简单的模式识别，frog要能进化出模式识别功能，即看到图来预测是要咬还是不咬，这就是最简单模式识别了，即原来的信号顺序是：
 * 图-(随机驱动)咬-甜, 或  图-(随机驱动)咬-苦，
 * 最终进化为: 图-甜-咬-甜，    图-苦-不咬-苦，也就是说根据图案来联想到甜或苦的感觉细胞激活，从而预判咬还是不咬，而无需等随机信号驱动，也无需等味觉信号传入（实际也不可能在咬之前有味觉)。
 * 
 * 
 * 这个功能通过  随机、饿、视、咬、苦、甜、忆这几类细胞来组合完成。   
 * 视细胞(Eye)是其中一环,即本类。Eye将外界信号（先简单用一个随机序号的条形码)投射成视网膜上的图像。 
 * 饿细胞，可以设定它永远活跃，即青蛙永远都吃不饱
 * 随机细胞，有一定机率活跃，作用是激活咬细胞
 * 咬细胞，如果活跃，产生实际的咬动作。Env会根据咬的图案有毒(随机序号被3除余1)扣分并激活苦细胞，或是无毒(随机序号被3除余2)加分并激活甜细胞
 * 苦细胞和甜细胞, 苦细胞和甜细胞是感觉信号，它由食物的毒性决定，只有咬下后才会激活
 * 忆细胞， 这种细胞的作用是记忆，当两个细胞在时间上有短期内的相关性时，就会在忆细胞上打出两个洞来，将来任一个细胞激活，就从洞里反向发送光子，从而实现信号的关联，即记忆功能。洞大小由信号重复数决定。
 *       因为洞大小可以很大，也可以很小直到遗忘消失，所以忆细胞接收和发送的脉冲信号可能强度有巨大差别，并不是0/1这种简单信号，而是有强度的信号。
 * 
 * 目前为最简化起见，食物图像用随机序号的二进制条形码图案代表，将来可扩展到2维食物图形或者二维文字图形，逻辑不需要改动。  
 * 
 */
public class Eye extends EnvObject {
    private static int codeMax = 1 <<Env.BRAIN_CUBE_SIZE-1;
    public static int code; //这里用code的二进制条形码代表任务图形，随便假设被3除后余1的有毒且苦，余2的可食且甜，余0的不

    @Override
    public void active(int screen, int step) {
        if (step % 10 != 0) //每隔10步在所有青蛙的视网膜上画一个图案
            return;
        code =  RandomUtils.nextInt(codeMax);
        Frog f;
        for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
            f = Env.frogs.get(i);
            SetImageWithCode(f, code);
        }
    }

    private static void SetImageWithCode(Frog f, int code) {//根据code数字在视网膜上画出它的二进制条形码
        //System.out.println(codeMax+"-"+code);
        long i = 1;
        for (int y = 0; y < Env.BRAIN_CUBE_SIZE; y++) {
            float engery = ((code & i) > 0) ? 9999f : 0;
            for (int z = 0; z < Env.BRAIN_CUBE_SIZE; z++)
                f.energys[0][y][z] = engery;
            i = i << 1;
        }
    }

   
}
