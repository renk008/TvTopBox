package top.renk.tvtopbox.thread;

import top.renk.tvtopbox.app.MyApplication;
import top.renk.tvtopbox.rxbus.RxBus;

/**
 * Created by pcdalao on 2017/11/28.
 */

public class ReadThread extends Thread {

    private boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }

    public ReadThread setOpen(boolean open) {
        isOpen = open;
        return this;
    }

    @Override
    public void run() {
        super.run();
        byte[] buffer = new byte[4096];

        while (true) {
            int length = MyApplication.getDriver().ReadData(buffer, 4096);
            if (length > 0) {
                //String recv = new String(buffer, 0, length);
                String string = toHexString(buffer, length);
                RxBus.get().send(9527, string);
            }
        }
    }


    /**
     * 将byte[]数组转化为String类型
     *
     * @param arg    需要转换的byte[]数组
     * @param length 需要转换的数组长度
     * @return 转换后的String队形
     */
    private String toHexString(byte[] arg, int length) {
        StringBuilder result = new StringBuilder();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result.append(Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])).append(" ");
            }
            return result.toString();
        }
        return "";
    }

    /**
     * 将String转化为byte[]数组
     *
     * @param arg 需要转换的String对象
     * @return 转换后的byte[]数组
     */
    private byte[] toByteArray(String arg) {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (char anArray : array) {
                if (anArray != ' ') {
                    NewArray[length] = anArray;
                    length++;
                }
            }
            /* 将char数组中的值转成一个实际的十进制数组 */
            int EvenLength = (length % 2 == 0) ? length : length + 1;
            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                /* 将 每个char的值每两个组成一个16进制数据 */
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[]{};
    }
}
