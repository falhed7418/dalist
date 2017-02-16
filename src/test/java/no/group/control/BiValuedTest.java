package no.group.control;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;


public class BiValuedTest {

    @Test
    public void testBiValued() {
        assertThat(solution(new int[]{}), is(0));
        assertThat(solution(new int[]{1, 1}), is(0));
        assertThat(solution(new int[]{1, 2}), is(2));
        assertThat(solution(new int[]{1, 2, 1}), is(3));
        assertThat(solution(new int[]{1, 2, 1, 2}), is(4));
        assertThat(solution(new int[]{1, 2, 0, 2}), is(3));
        assertThat(solution(new int[]{0, 1, 1, 3, 3, 3}), is(5));
        assertThat(solution(new int[]{0, 2, 1, 3, 3, 3}), is(4));
        assertThat(solution(new int[]{6, 5, 4, 3, 2, 1}), is(2));
        assertThat(solution(new int[]{6, 5, 4, 3, 2, 2}), is(3));
        assertThat(solution(new int[]{6, 5, 4, 3, 2, 2, 3, 3}), is(5));
        assertThat(solution(new int[]{6, 5, 4, 3, 2, 1, 3, 3, 2}), is(3));
    }

    private int solution(int[] arr) {

        if (arr == null || arr.length < 2) {
            return 0;
        }

        if (arr.length == 2 && arr[0] == arr[1]) {
            return 0;
        }

        int max = 0;

        for (int index = 0; index < arr.length -1 ; index ++) {
            int current = countBiValued(arr, index);
            if (max < current) {
                max = current;
            }
        }

        return max;

    }

    private int countBiValued(int[] arr, int offset) {
        int first = arr[offset];
        int second = 0;
        boolean second_assigned = false;
        int countBiVal = 2;
        if (arr[offset + 1] != arr[offset]) {
            second_assigned = true;
            second = arr[offset + 1];
        }

        
        for (int i = offset + 2; i < arr.length; i++) {
            if (!second_assigned && arr[i] != first) {
                second_assigned = true;
                second = arr[i];
                countBiVal++;
                continue;
            }
            if (!second_assigned && arr[i] == first) {
                countBiVal++;
                continue;
            }
            if (second_assigned && arr[i] == first || arr[i] == second) {
                countBiVal++;
                continue;
            }
 
            break;
        }

        if (second_assigned) {
            return countBiVal;
        }
        return 0;
    }

}

