package no.group.control;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;


public class EquilibriumTest {

    @Test
    public void testEquilibrium() {
        assertThat(solution(new int[]{}), is(-1));
        assertThat(solution(new int[]{1}), is(0));
        assertThat(solution(new int[]{1, 1}), is(0));
        assertThat(solution(new int[]{1, 0, 1}), is(1));
        assertThat(solution(new int[]{-3, -1, 0, -2, -2}), is(2));
    }

    int solution(int[] arr) {

        if (arr == null || arr.length == 0) {
            return -1;
        }

        if (arr.length == 1) {
            return 0; 
        }

        if (arr.length == 2 && arr[0] == arr[1]) {
            return 0;
        }

        for(int i = 0; i < arr.length; i++) {
            if (sumbehind(arr, i) == sumahead(arr, i)) {
                return i;
            }
        }

        return -1;
    }

    int sumbehind(int[] arr, int index) {

        int sum = 0;
        for (int i = 0; i < index; i++) {
            sum += arr[i];
        }

        return sum;
    }

    int sumahead(int[] arr, int index) {
        int sum = 0;

        for (int i = arr.length -1; i > index; i--) {
            sum += arr[i];
            System.out.println("sum is:" +sum);
        }

        return sum;
    }
    
}
