package nexus;

import lombok.SneakyThrows;
import nexus.engine.CoreEngine;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        String resources = "src/main/resources";
        String title = "NexusEngine";
        int width = 1080, height = 720;
        /*
         * if only 3 parameters are specified,
         * then we use third argument or width,
         * and multiple it by the aspect ratio to get the height
         * ex. 1920 is passed, then 1920 * 0.5625 = 1080,
         * or if 2560 is passed, then the height would be 1440
         */
        float aspectRatio = 9.0f / 16.0f; //16 by 9 aspect
        boolean resizable = false;
        boolean vSync = false;
        int startMonitor = -1; //if -1, then the start monitor will the primary monitor
        switch (args.length) {
            case 1:
                resources = args[0];
                break;
            case 2:
                resources = args[0];
                title = args[1];
                break;
            case 3:
                resources = args[0];
                title = args[1];
                width = Integer.parseInt(args[2]);
                height = (int) (width * aspectRatio);
                break;
            case 4:
                resources = args[0];
                title = args[1];
                width = Integer.parseInt(args[2]);
                height = Integer.parseInt(args[3]);
                break;
            case 5:
                resources = args[0];
                title = args[1];
                width = Integer.parseInt(args[2]);
                height = Integer.parseInt(args[3]);
                resizable = Boolean.parseBoolean(args[4]);
                break;
            case 6:
                resources = args[0];
                title = args[1];
                width = Integer.parseInt(args[2]);
                height = Integer.parseInt(args[3]);
                resizable = Boolean.parseBoolean(args[4]);
                vSync = Boolean.parseBoolean(args[5]);
                break;
            case 7:
                resources = args[0];
                title = args[1];
                width = Integer.parseInt(args[2]);
                height = Integer.parseInt(args[3]);
                resizable = Boolean.parseBoolean(args[4]);
                vSync = Boolean.parseBoolean(args[5]);
                startMonitor = Integer.parseInt(args[6]);
                break;
        }

        new CoreEngine(resources, title, width, height, resizable, vSync, startMonitor);
    }
}
