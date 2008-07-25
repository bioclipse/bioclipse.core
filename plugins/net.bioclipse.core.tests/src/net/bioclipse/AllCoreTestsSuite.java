package net.bioclipse;

import net.bioclipse.core.ResourcePathTransformerTest;
import net.bioclipse.core.domain.BioListTest;
import net.bioclipse.recording.RecordTest;
import net.bioclipse.recording.ScriptGenerationTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { BioListTest.class,
                         RecordTest.class,
                         ScriptGenerationTests.class, } )
public class AllCoreTestsSuite {

}
