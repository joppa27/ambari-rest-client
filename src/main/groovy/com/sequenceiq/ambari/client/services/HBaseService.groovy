/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sequenceiq.ambari.client.services

import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import groovyx.net.http.ContentType

@Slf4j
trait HBaseService extends ClusterService {

  /**
   * Decommission the HBase Region Server on the given hosts.
   */
  def int decommissionHBaseRegionServers(List<String> hosts) {
    decommission(hosts, 'HBASE_REGIONSERVER', 'HBASE', 'HBASE_MASTER')
  }

  /**
   * Set the HBase Region servers to maintenance mode and back.
   *
   * @param hostNames names of the hosts which is running the Region server
   * @param mode true to maintenance mode false to active mode
   */
  def void setHBaseRegionServersToMaintenance(List<String> hostNames, boolean mode) {
    hostNames.each {
      setHBaseRegionServerToMaintenance(it, mode)
    }
  }

  /**
   * Set the HBase Region server to maintenance mode and back.
   *
   * @param hostName name of the host which is running the Region server
   * @param mode true to maintenance mode false to active mode
   */
  def void setHBaseRegionServerToMaintenance(String hostName, boolean mode) {
    def reqInfo = ['RequestInfo': ['context': 'Turn On Maintenance Mode for RegionServer'],
                   'Body'       : ['HostRoles': ['maintenance_state': mode ? 'ON' : 'OFF']]]
    def Map<String, ?> putRequestMap = [:]
    putRequestMap.put('requestContentType', ContentType.URLENC)
    putRequestMap.put('path', "clusters/${getClusterName()}/hosts/$hostName/host_components/HBASE_REGIONSERVER")
    putRequestMap.put('body', new JsonBuilder(reqInfo).toPrettyString());

    ambari.put(putRequestMap)
  }

  /**
   * Returns the hostnames of the desired Region servers and their state
   *
   * @param hostFilter filter for which hosts are interested
   * @return map key - hostname value - state
   */
  def Map<String, String> getHBaseRegionServersState(List<String> hosts) {
    def res = [:]
    hosts.each {
      def rs = utils.getAllResources("hosts/$it/host_components/HBASE_REGIONSERVER")
      res << ["$it": rs.HostRoles.state]
    }
    res
  }
}