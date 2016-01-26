/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.addon.maven.projects.archetype.ui;

/**
 * Represents a WizardStep that can be used by a ProjectType which resolves to a single constant
 * archetype using maven coordinates.
 */
public abstract class ConstantArchetypeSelectionWizardStep extends ArchetypeSelectionWizardStepSupport {
    private String archetypeRepository;
    private String archetypeGroupId;
    private String archetypeArtifactId;
    private String archetypeVersion;

    public ConstantArchetypeSelectionWizardStep() {
    }

    public ConstantArchetypeSelectionWizardStep(String archetypeGroupId, String archetypeArtifactId, String archetypeVersion) {
        this(archetypeGroupId, archetypeArtifactId, archetypeVersion, null);
    }

    public ConstantArchetypeSelectionWizardStep(String archetypeGroupId, String archetypeArtifactId, String archetypeVersion, String archetypeRepository) {
        this.archetypeGroupId = archetypeGroupId;
        this.archetypeArtifactId = archetypeArtifactId;
        this.archetypeVersion = archetypeVersion;
        this.archetypeRepository = archetypeRepository;
    }

    @Override
    protected String getArchetypeRepository() {
        return archetypeRepository;
    }

    @Override
    protected String getArchetypeVersion() {
        return archetypeVersion;
    }

    @Override
    protected String getArchetypeArtifactId() {
        return archetypeArtifactId;
    }

    @Override
    protected String getArchetypeGroupId() {
        return archetypeGroupId;
    }

    protected void setArchetypeArtifactId(String archetypeArtifactId) {
        this.archetypeArtifactId = archetypeArtifactId;
    }

    protected void setArchetypeGroupId(String archetypeGroupId) {
        this.archetypeGroupId = archetypeGroupId;
    }

    protected void setArchetypeRepository(String archetypeRepository) {
        this.archetypeRepository = archetypeRepository;
    }

    protected void setArchetypeVersion(String archetypeVersion) {
        this.archetypeVersion = archetypeVersion;
    }
}
