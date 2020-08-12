package com.example.bigfilefinder3;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FileManagerTests {
    @Test
    public void removeDuplicatePaths_ReturnsOnlyMostGenericFolderPaths() {
        // given
        List<String> inputPaths = Arrays.asList("A/B/C", "A/B", "E", "A/B/C/D", "E/F", "E/F/G/H");
        List<String> expectedResultPaths = Arrays.asList("E", "A/B");

        // when
        List<String> resultPaths = FileManager.removeDuplicatePaths(inputPaths);

        // then
        assertThat(resultPaths, is(expectedResultPaths));
    }
}