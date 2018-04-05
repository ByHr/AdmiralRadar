/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package org.apache.batik.extension.svg;

import org.apache.batik.bridge.TextNode;
import org.apache.batik.bridge.TextPainter;


/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org">deweese</a>
 * @version $Id: FlowExtTextNode.java 1733416 2016-03-03 07:07:13Z gadams $
 */
public class FlowExtTextNode extends TextNode{

    public FlowExtTextNode() {
        textPainter = FlowExtTextPainter.getInstance();
    }

    public void setTextPainter(TextPainter textPainter) {
        if (textPainter == null)
            this.textPainter = FlowExtTextPainter.getInstance();
        else
            this.textPainter = textPainter;
    }

}
