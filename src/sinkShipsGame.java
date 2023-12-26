import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

// sinkShipsGame
// 2023 Rand7Y9Z@gmail.com

public class sinkShipsGame {

    //-----------------------------------------------------------------------------------------------------
    public static String programName = "sinkShipsGame";
    public static double version = 1.0;
    //-----------------------------------------------------------------------------------------------------


    private static Random generator = new Random();
    private static Scanner in = new Scanner(System.in);

    // these two variables define the size of the play field
    public static int playFieldHeigth;
    public static int playFieldWidth;

    // these two variables are going to contain the needed values for the playField and the startField
    public static char[][] playField;
    public static char[][] startField;

    public static int nrOfShips;
    public static int nrOfShipsFields;
    public static int nrOfShipsFieldsFound = 0;
    public static int ShipsMaxLength;
    public static int NrOfTries = 0;
    public static boolean continueGame;


    public static void main(String[] args) {

        System.out.println(programName + " " + version + "\n");

        // here it gets the number of rows
        boolean ValidInputRow = false;
        while (!ValidInputRow) {
            System.out.print("How many rows do you want?: ");
            String scCol = in.nextLine();
            try {
                playFieldHeigth = Integer.parseInt(scCol);
                if (playFieldHeigth > 3 && playFieldHeigth < 100) {
                    ValidInputRow = true;
                } else {
                    System.out.println("Invalid Input! Please enter a number between 4 and 99!");
                }

            } catch (Exception e) {
                System.out.println("Invalid Input!");
            }
            System.out.println();
        }

        // here it gets the number of columns
        boolean ValidInputCol = false;
        while (!ValidInputCol) {
            System.out.print("How many columns do you want?: ");
            String scCol = in.nextLine();
            try {
                playFieldWidth = Integer.parseInt(scCol);
                if (playFieldWidth > 3 && playFieldWidth < 27) {
                    ValidInputCol = true;
                } else {
                    System.out.println("Invalid Input! Please enter a number between 4 and 99!");
                }

            } catch (Exception e) {
                System.out.println("Invalid Input!");
            }
            System.out.println();
        }

        // this part gets the maximum length of the created ships
        boolean ValidInputShipsMaxLength = false;
        while (!ValidInputShipsMaxLength) {
            System.out.print("What should be the maximum ship length?: ");
            String scCol = in.nextLine();
            try {
                ShipsMaxLength = Integer.parseInt(scCol);
                if (ShipsMaxLength > 0 && ShipsMaxLength <= getMinimum(new int[]{5, playFieldHeigth, playFieldWidth, playFieldWidth, playFieldHeigth * playFieldWidth / 8})) {
                    ValidInputShipsMaxLength = true;
                } else {
                    System.out.printf("Invalid Input! Please enter a number between 1 and %d  \n", getMinimum(new int[]{5, playFieldHeigth, playFieldWidth, playFieldHeigth * playFieldWidth / 8}));
                }

            } catch (Exception e) {
                System.out.println("Invalid Input!");
            }
            System.out.println();
        }

        nrOfShips = playFieldHeigth * playFieldWidth / 12;  //Here I calculate the number of ships that get to be placed
        startField = createStartField(nrOfShips);           // this together with the next part saves the two needed fields in global variables
        playField = createPlayField();

        continueGame = true;

        while (continueGame) {
            System.out.print("Your guess is (rowColum or end): ");
            String checkStr = in.nextLine();
            System.out.println();
            if (checkStr.equals("end") || checkStr.equals("End")) {
                continueGame = false;
                printSolution();
            } else {
                if (checkStr.length() > 3 || checkStr.length() < 2 || getCol(checkStr) == -1 || getRow(checkStr) == -1 || getRow(checkStr) >= playField.length || getCol(checkStr) >= playField[0].length//
                ) {
                    System.out.println("---> Invalid Input! " + checkStr);

                } else {
                    checkPlayField(getRow(checkStr), getCol(checkStr));
                    NrOfTries++;
                }
                printPlayField();
            }
            if (nrOfShipsFieldsFound == nrOfShipsFields) {
                System.out.println("finished!");
                continueGame = false;
            }
        }

    }

    /**
     * this methode creates the start Field with the "ships" in it
     */
    public static char[][] createStartField(int nShips) {

        char[][] ch = new char[playFieldHeigth][playFieldWidth];
        IntStream.range(0, playFieldHeigth).forEach(y -> Arrays.fill(ch[y], '0'));


        int[] ShipsData = sizeAndNumbersOfShips(ShipsMaxLength, nShips);


        // only for the ships with a +
        int counter1 = 0;
        while (counter1 < ShipsData[0]) {
            int RandomY = (int) ((playFieldHeigth) * Math.random());
            int RandomX = (int) ((playFieldWidth) * Math.random());
            if (ch[RandomY][RandomX] == '0' && isAllowedPosition(ch, RandomY, RandomX)) {
                ch[RandomY][RandomX] = '+';
                counter1++;
                nrOfShipsFields++;
            }
        }


        // for the ships with a -;
        for (int i = 1; i < ShipsData.length; i++) {
            int counter = 0;
            while (counter < ShipsData[i]) {
                try {
                    int RandomY = (int) ((playFieldHeigth) * Math.random());
                    int RandomX = (int) ((playFieldWidth) * Math.random());
                    int VektorData = 1 + (int) ((2) * Math.random());  // 1 makes it go down, 2 makes it go to the right


                    if (VektorData == 1) {
                        if ((RandomY + i + 1 < playFieldWidth) && isAllowedPositionForBiggerShips(ch, RandomY, RandomY + i, RandomX, RandomX)) {
                            IntStream.rangeClosed(0, i).forEach(j -> ch[RandomY + j][RandomX] = '-');
                            counter++;
                            nrOfShipsFields += i + 1;
                        }
                    } else if (VektorData == 2) {
                        if ((RandomX + i + 1 < playFieldHeigth) && isAllowedPositionForBiggerShips(ch, RandomY, RandomY, RandomX, RandomX + i)) {
                            IntStream.rangeClosed(0, i).forEach(j -> ch[RandomY][RandomX + j] = '-');
                            counter++;
                            nrOfShipsFields += i + 1;
                        }
                    }

                } catch (Exception ignore) {
                }
            }
        }


        return ch;

    }

    /**
     * this methode creates an array that contains the information how many ships of which size have to be generated in createStartField
     *
     * @param maxLength is the maximal length a "ship" can have
     * @param NrShips   is how many ships have to be generated
     * @return is an integer array with the length 5
     */
    public static int[] sizeAndNumbersOfShips(int maxLength, int NrShips) {
        int[] r = new int[5];
        boolean run = true;

        while (run) {
            IntStream.range(0, NrShips).map(i -> 1 + (int) ((maxLength) * Math.random())).forEach(size -> r[size - 1]++);
            run = (getSumme(r) >= playFieldWidth * playFieldHeigth / 3);
        }

        return r;
    }


    public static boolean isAllowedPositionForBiggerShips(char[][] a, int yPosStart, int yPosEnd, int xPosStart, int xPosEnd) {
        return IntStream.rangeClosed(yPosStart, yPosEnd)
                .noneMatch(i -> IntStream.rangeClosed(xPosStart, xPosEnd).anyMatch(j -> !isAllowedPosition(a, i, j)));
    }

    /**
     * this methode checks if the a[yPos][xPos] is a valid position for a "ship(spart)" to be "placed".
     * (the one above uses this one for bigger "ships")
     */
    public static boolean isAllowedPosition(char[][] a, int yPos, int xPos) {
        try {
            if (a[yPos - 1][xPos] == '+' || a[yPos - 1][xPos] == '-') return false;

        } catch (Exception ignored) {
        }

        try {
            if (a[yPos + 1][xPos] == '+' || a[yPos + 1][xPos] == '-') return false;

        } catch (Exception ignored) {
        }

        try {
            if (a[yPos][xPos - 1] == '+' || a[yPos][xPos - 1] == '-') return false;


        } catch (Exception ignored) {
        }

        try {
            if (a[yPos][xPos + 1] == '+' || a[yPos][xPos + 1] == '-') return false;

        } catch (Exception ignored) {
        }

        return true;
    }

    /**
     * this methode creates the playField ( the field the user is going to be able to see)
     *
     * @return is a two-dimensional character array
     */
    public static char[][] createPlayField() {
        char[][] ch = new char[playFieldHeigth][playFieldWidth];
        IntStream.range(0, playFieldHeigth).forEach(y -> Arrays.fill(ch[y], '?'));
        return ch;
    }

    /**
     * this methode indexes the colum value
     */
    public static int getCol(String s) {
        char c = (s.toLowerCase()).charAt(s.length() - 1);
        return (c < 'a' || c > 'z') ? -1 : c - 'a';
    }

    /**
     * this methode indexes the row value
     */
    public static int getRow(String s) {
        if ((s.length() == 3 && !Character.isDigit(s.charAt(1))) || (s.length() == 3 && !Character.isDigit(s.charAt(0)))) {
            return -1;
        } else if (s.length() == 3) {
            try {
                return Integer.parseInt(s.substring(0, 2)) - 1;
            } catch (Exception e) {
                return -1;
            }

        } else {
            try {
                return Integer.parseInt(s.substring(0, 1)) - 1;
            } catch (Exception e) {
                return -1;
            }
        }
    }

    /**
     * this methode prints out the playField in the console
     */
    public static void printPlayField() {
        StringBuilder s = new StringBuilder();
        s.append("   ");
        for (int i = 0; i < playField[0].length; i++) {
            s.append((char) (i + 97)).append(" ");
        }
        s.append("\n");
        for (int y = 0; y < playFieldHeigth; y++) {
            s.append(String.format("%2d ", y + 1));
            for (int x = 0; x < playFieldWidth; x++) {
                s.append(playField[y][x]).append(" ");
            }
            s.append("\n");
        }
        System.out.println(s);
    }

    /**
     * this methode prints out the solution if the user decides to end the game before all fields occupied with ships are found
     */
    public static void printSolution() {
        StringBuilder s = new StringBuilder();
        s.append("   ");
        for (int i = 0; i < playFieldWidth; i++) {
            s.append((char) (i + 'a')).append(" ");
        }
        s.append("\n");
        for (int y = 0; y < playField.length; y++) {
            s.append(String.format("%2d", y + 1)).append(" ");
            for (int x = 0; x < playField[y].length; x++) {
                if (startField[y][x] == '+' && playField[y][x] == '?') {
                    s.append("+ ");
                } else if (startField[y][x] == '-') {
                    s.append("- ");
                } else {
                    s.append(playField[y][x]).append(" ");
                }
            }
            s.append("\n");
        }

        System.out.println(s);
        System.out.println("You used " + NrOfTries + " tries to find " + nrOfShipsFieldsFound + " of " + nrOfShipsFields + " fields occupied with ships!");
        System.out.println("Sadly, you surrendered. \nThe ships, you didn't hit are marked with '+' and '-'.");
    }

    /**
     * this methode checks the value in playField/StartField at the given Position row/col
     */
    public static void checkPlayField(int row, int col) {

        if (playField[row][col] != '?') {
            System.out.println("NOT AGAIN!");
        } else {
            if (startField[row][col] == '+' || startField[row][col] == '-') {
                nrOfShipsFieldsFound++;
                System.out.println("hit!");
                if (startField[row][col] == '+') {
                    playField[row][col] = '#';
                } else {
                    playField[row][col] = '*';
                }
            } else {
                System.out.println("miss!");
                playField[row][col] = '.';
            }
        }

    }

    /**
     * this methode returns the lowest value found in the array a
     */
    public static int getMinimum(int[] a) {
        int s = a[0];

        for (int j : a) {
            if (j < s) {
                s = j;
            }
        }

        return s;
    }

    /**
     * this methode returns the sum of the values in a
     */
    public static int getSumme(int[] a) {
        int s = 0;

        for (int j : a) {
            s += j;
        }

        return s;
    }
}