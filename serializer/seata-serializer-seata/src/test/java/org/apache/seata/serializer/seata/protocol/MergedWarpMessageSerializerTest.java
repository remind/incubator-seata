/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.seata.serializer.seata.protocol;

import java.util.ArrayList;
import java.util.List;

import org.apache.seata.core.protocol.AbstractMessage;
import org.apache.seata.core.protocol.MergedWarpMessage;
import org.apache.seata.core.protocol.transaction.GlobalBeginRequest;
import org.apache.seata.serializer.seata.SeataSerializer;
import org.junit.jupiter.api.Test;
import org.apache.seata.core.protocol.ProtocolConstants;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * The type Merged warp message codec test.
 *
 */
public class MergedWarpMessageSerializerTest {

    /**
     * The Seata codec.
     */
    SeataSerializer seataSerializer = new SeataSerializer(ProtocolConstants.VERSION);

    /**
     * Test codec.
     */
    @Test
    public void test_codec(){
        MergedWarpMessage mergedWarpMessage = new MergedWarpMessage();
        final ArrayList<AbstractMessage> msgs = new ArrayList<>();
        final List<Integer> msgIds = new ArrayList<>();
        final GlobalBeginRequest globalBeginRequest1 = buildGlobalBeginRequest("x1");
        final GlobalBeginRequest globalBeginRequest2 = buildGlobalBeginRequest("x2");
        msgs.add(globalBeginRequest1);
        msgIds.add(1);
        msgs.add(globalBeginRequest2);
        msgIds.add(2);
        mergedWarpMessage.msgs = msgs;
        mergedWarpMessage.msgIds = msgIds;


        byte[] body = seataSerializer.serialize(mergedWarpMessage);

        MergedWarpMessage mergedWarpMessage2 = seataSerializer.deserialize(body);
        assertThat(mergedWarpMessage2.msgs.size()).isEqualTo(mergedWarpMessage.msgs.size());

        assertThat(mergedWarpMessage2.msgIds.size()).isEqualTo(2);
        assertThat(mergedWarpMessage2.msgIds.get(0)).isEqualTo(1);
        assertThat(mergedWarpMessage2.msgIds.get(1)).isEqualTo(2);

        GlobalBeginRequest globalBeginRequest21 = (GlobalBeginRequest) mergedWarpMessage2.msgs.get(0);
        assertThat(globalBeginRequest21.getTimeout()).isEqualTo(globalBeginRequest1.getTimeout());
        assertThat(globalBeginRequest21.getTransactionName()).isEqualTo(globalBeginRequest1.getTransactionName());


        GlobalBeginRequest globalBeginRequest22 = (GlobalBeginRequest) mergedWarpMessage2.msgs.get(1);
        assertThat(globalBeginRequest22.getTimeout()).isEqualTo(globalBeginRequest2.getTimeout());
        assertThat(globalBeginRequest22.getTransactionName()).isEqualTo(globalBeginRequest2.getTransactionName());

    }

    private GlobalBeginRequest buildGlobalBeginRequest(String name) {
        final GlobalBeginRequest globalBeginRequest = new GlobalBeginRequest();
        globalBeginRequest.setTransactionName(name);
        globalBeginRequest.setTimeout(3000);
        return globalBeginRequest;
    }
}
