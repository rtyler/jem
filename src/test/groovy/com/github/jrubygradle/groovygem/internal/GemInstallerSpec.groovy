package com.github.jrubygradle.groovygem.internal

import spock.lang.*

import java.nio.file.Files
import java.nio.file.Path

class GemInstallerSpec extends Specification {
    final String FIXTURES_ROOT = new File(['src', 'test', 'resources'].join(File.separator)).absolutePath
    final String GEM_FIXTURE = [FIXTURES_ROOT, 'thor-0.19.1.gem'].join(File.separator)

    GemInstaller installer
    Path installDirPath = Files.createTempDirectory("geminstallerspec")
    String installDir = installDirPath as String

    /** Return true if the given rootDir looks like a Gem home dir */
    boolean isGemHome(String rootDir) {
        ['bin', 'build_info', 'cache', 'doc', 'extensions',
         'gems', 'specifications'].each { String madeDir ->

            File fullPath = new File(installDir, madeDir)

            if ( (!fullPath.exists()) || (!fullPath.isDirectory()) ) {
                return false
            }
        }
        return true
    }

    def "mkdirs() should create the necessary directories for a GEM_HOME"() {
        given:
        installer = new GemInstaller(installDir, [new File(GEM_FIXTURE)])

        when: "mkdirs() is invoked"
        installer.mkdirs()

        then: "the dir should exist as a directory"
        isGemHome(installDir)
    }

    def "isValidGem() should return false if the given file doesn't end in .gem"() {
        given:
        Path fakeGemPath = Files.createTempFile('geminstallerspec', null)
        File fakeGem = new File(fakeGemPath as String)
        installer = new GemInstaller(installDir, [fakeGem])

        expect:
        !installer.isValidGem(fakeGem)
    }

    def "isValidGem() should return false if the given file isn't valid"() {
        given:
        Path fakeGemPath = Files.createTempFile('geminstallerspec', '.gem')
        File fakeGem = new File(fakeGemPath as String)
        installer = new GemInstaller(installDir, [fakeGem])

        expect:
        !installer.isValidGem(fakeGem)
    }

    def "isvalidGem should return true with a given file that is a gem"() {
        given:
        File gem = new File(GEM_FIXTURE)
        installer = new GemInstaller(installDir, [gem])

        expect:
        installer.isValidGem(gem)
    }
}
