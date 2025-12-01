/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.integtests.tooling.r940

import org.gradle.integtests.tooling.fixture.TargetGradleVersion
import org.gradle.integtests.tooling.fixture.ToolingApiSpecification
import org.gradle.integtests.tooling.fixture.ToolingApiVersion
import org.gradle.tooling.model.UnsupportedMethodException
import org.gradle.tooling.model.build.BuildEnvironment

@ToolingApiVersion(">=9.4.0")
@TargetGradleVersion(">=9.4.0")
class BuildEnvironmentVersionInfoCrossVersionSpec extends ToolingApiSpecification {

    @TargetGradleVersion('>=4.0 <9.4.0')
    def "cannot query old distribution for version info"() {
        when:
        withConnection { connection ->
            connection.getModel(BuildEnvironment).versionInfo
        }

        then:
        thrown(UnsupportedMethodException)
    }

    def "can fetch version info via model query"() {
        when:
        def versionInfo = withConnection { connection ->
            connection.getModel(BuildEnvironment).versionInfo
        }

        then:
        assertVersionInfoHasSensibleContent(versionInfo)
    }

    def "can fetch version info via build action"() {
        when:
        def versionInfo = withConnection { connection ->
            connection.action(new FetchBuildEnvironmentVersionInfoAction()).run()
        }

        then:
        assertVersionInfoHasSensibleContent(versionInfo)
    }

    private static void assertVersionInfoHasSensibleContent(String versionInfo) {
        assert versionInfo.contains("Gradle")
        assert versionInfo.contains("Build time:")
        assert versionInfo.contains("Revision:")
        assert versionInfo.contains("Kotlin:")
    }
}
