/*
 *  Copyright 2014 TWO SIGMA INVESTMENTS, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.twosigma.beaker.core.module.config;

import java.util.Map;

/**
 * BeakerConfig
 *
 */
public interface BeakerConfig {
  public String getInstallDirectory(); // a.k.a beaker core directory
  public String getPluginDirectory(); // where to search for plugin executables and starting scripts
  public String getDotDirectory(); // where temp directory and variable storage and cache go
  public String getNginxDirectory(); // where to locate beaker specific nginx configs and scripts
  public String getNginxBinDirectory(); // where to locate nginx executable
  public String getNginxServDirectory(); // the root of nginx server
  public String getNginxExtraRules();
  public Integer getPortBase();
  public Integer getReservedPortCount();
  public Boolean getUseKerberos();
  public String getConfigFileUrl();
  public String getDefaultNotebookUrl();
  public String getRecentNotebooksFileUrl();
  public String getSessionBackupsDirectory();
  public Map<String, String> getPluginLocations();
  public Map<String, String> getPluginOptions();
  public Map<String, String[]> getPluginEnvps();
}
