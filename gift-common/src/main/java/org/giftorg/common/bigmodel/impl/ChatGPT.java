/**
 * Copyright 2023 GiftOrg Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.giftorg.common.bigmodel.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.config.Config;
import org.giftorg.common.tokenpool.APITaskResult;
import org.giftorg.common.tokenpool.TokenPool;
import org.giftorg.common.utils.MarkdownUtil;
import org.giftorg.common.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ChatGPT implements BigModel {
    private static final String baseUrl = StringUtil.trimEnd(Config.chatGPTConfig.getHost(), "/");
    private static final List<String> apiKeys = Config.chatGPTConfig.getApiKeys();
    private static final String model = Config.chatGPTConfig.getModel();
    private static final String apiUrl = baseUrl + "/v1/chat/completions";
    private static final TokenPool tokenPool = TokenPool.getTokenPool(apiKeys, 64, 3);

    /**
     * 聊天接口，接收一个消息列表，返回大模型回复的消息
     */
    public String chat(List<Message> messages) throws Exception {
        Request req = new Request(model, messages);
        AtomicReference<HttpResponse> atResp = new AtomicReference<>();
        log.info("chat request: {}", messages);

        APITaskResult result = tokenPool.runTask(token -> {
            HttpResponse resp = HttpRequest.post(apiUrl)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONBytes(req))
                    .execute();
            atResp.set(resp);
        });

        if (!result.getSuccess()) {
            throw new RuntimeException(result.getException());
        }

        HttpResponse resp = atResp.get();
        Response res = JSONUtil.toBean(resp.body(), Response.class);
        if (res.choices == null || res.choices.isEmpty()) {
            log.error("chat response error, response.body: {}", resp.body());
            throw new RuntimeException("chat response error, response.body: " + resp.body());
        }

        log.info("chat answer: {}", res.choices.get(0).message.content);
        return res.choices.get(0).message.content;
    }

    @Override
    public List<Double> textEmbedding(String prompt) throws Exception {
        throw new Exception("not implemented");
    }

    /**
     * ChatGPT 请求体
     */
    public static class Request {
        public String model;
        public List<Message> messages;

        public Request(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    /**
     * ChatGPT 响应体
     */
    @ToString
    public static class Response {
        public List<Choice> choices;
    }

    @ToString
    public static class Choice {
        public Message message;
    }

    public static void main(String[] args) throws Exception {
        ChatGPT gpt = new ChatGPT();
        ArrayList<BigModel.Message> messages = new ArrayList<>();
//        messages.add(new BigModel.Message("user", "hello"));;
//        String answer = gpt.chat(messages);
//        System.out.println(answer);


        String q = "![Flexmark Icon Logo](assets/images/flexmark-icon-logo.png) flexmark-java\n" +
                "=========================================================================\n" +
                "\n" +
                "**flexmark-java** is a Java implementation of **[CommonMark (spec 0.28)]** parser using the\n" +
                "blocks first, inlines after Markdown parsing architecture.\n" +
                "\n" +
                "Its strengths are speed, flexibility, Markdown source element based AST with details of the\n" +
                "source position down to individual characters of lexemes that make up the element and\n" +
                "extensibility.\n" +
                "\n" +
                "The API allows granular control of the parsing process and is optimized for parsing with a large\n" +
                "number of installed extensions. The parser and extensions come with plenty of options for parser\n" +
                "behavior and HTML rendering variations. The end goal is to have the parser and renderer be able\n" +
                "to mimic other parsers with great degree of accuracy. This is now partially complete with the\n" +
                "implementation of [Markdown Processor Emulation](#markdown-processor-emulation)\n" +
                "\n" +
                "Motivation for this project was the need to replace [pegdown] parser in my [Markdown Navigator]\n" +
                "plugin for JetBrains IDEs. [pegdown] has a great feature set but its speed in general is less\n" +
                "than ideal and for pathological input either hangs or practically hangs during parsing.\n" +
                "\n" +
                ":warning: **Version 0.60.0** has breaking changes due to re-organization, renaming, clean up and\n" +
                "optimization of implementation classes. Changes are detailed in\n" +
                "[Version-0.60.0-Changes](../../wiki/Version-0.60.0-Changes).\n" +
                "\n" +
                "### latest [![Maven Central status](https://img.shields.io/maven-central/v/com.vladsch.flexmark/flexmark.svg)](https://search.maven.org/search?q=g:com.vladsch.flexmark)<!-- @IGNORE PREVIOUS: link --> [![Javadocs](https://www.javadoc.io/badge/com.vladsch.flexmark/flexmark.svg)](https://www.javadoc.io/doc/com.vladsch.flexmark/flexmark)\n" +
                "\n" +
                "### Requirements\n" +
                "\n" +
                "* For Versions 0.62.2 or below, Java 8 or above, Java 9+ compatible. For Versions 0.64.0 or\n" +
                "  above, Java 11 or above.\n" +
                "* The project is on Maven: `com.vladsch.flexmark`\n" +
                "* The core has no dependencies other than `org.jetbrains:annotations:24.0.1`. For extensions, see\n" +
                "  extension description below.\n" +
                "\n" +
                "  The API is still evolving to accommodate new extensions and functionality.\n" +
                "\n" +
                "### Quick Start\n" +
                "\n" +
                "For Maven, add `flexmark-all` as a dependency which includes core and all modules to the\n" +
                "following sample:\n" +
                "\n" +
                "```xml\n" +
                "<dependency>\n" +
                "    <groupId>com.vladsch.flexmark</groupId>\n" +
                "    <artifactId>flexmark-all</artifactId>\n" +
                "    <version>0.64.8</version>\n" +
                "</dependency>\n" +
                "```\n" +
                "\n" +
                "Source:\n" +
                "[BasicSample.java](flexmark-java-samples/src/com/vladsch/flexmark/java/samples/BasicSample.java)\n" +
                "\n" +
                "```java\n" +
                "package com.vladsch.flexmark.samples;\n" +
                "\n" +
                "import com.vladsch.flexmark.util.ast.Node;\n" +
                "import com.vladsch.flexmark.html.HtmlRenderer;\n" +
                "import com.vladsch.flexmark.parser.Parser;\n" +
                "import com.vladsch.flexmark.util.data.MutableDataSet;\n" +
                "\n" +
                "public class BasicSample {\n" +
                "    public static void main(String[] args) {\n" +
                "        MutableDataSet options = new MutableDataSet();\n" +
                "\n" +
                "        // uncomment to set optional extensions\n" +
                "        //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));\n" +
                "\n" +
                "        // uncomment to convert soft-breaks to hard breaks\n" +
                "        //options.set(HtmlRenderer.SOFT_BREAK, \"<br />\\n\");\n" +
                "\n" +
                "        Parser parser = Parser.builder(options).build();\n" +
                "        HtmlRenderer renderer = HtmlRenderer.builder(options).build();\n" +
                "\n" +
                "        // You can re-use parser and renderer instances\n" +
                "        Node document = parser.parse(\"This is *Sparta*\");\n" +
                "        String html = renderer.render(document);  // \"<p>This is <em>Sparta</em></p>\\n\"\n" +
                "        System.out.println(html);\n" +
                "    }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "#### Building via Gradle\n" +
                "\n" +
                "```shell\n" +
                "implementation 'com.vladsch.flexmark:flexmark-all:0.64.8'\n" +
                "```\n" +
                "\n" +
                "#### Building with Android Studio\n" +
                "\n" +
                "Additional settings due to duplicate files:\n" +
                "\n" +
                "```groovy\n" +
                "packagingOptions {\n" +
                "    exclude 'META-INF/LICENSE-LGPL-2.1.txt'\n" +
                "    exclude 'META-INF/LICENSE-LGPL-3.txt'\n" +
                "    exclude 'META-INF/LICENSE-W3C-TEST'\n" +
                "    exclude 'META-INF/DEPENDENCIES'\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "More information can be found in the documentation:  \n" +
                "[Wiki Home](../../wiki) &nbsp;&nbsp;&nbsp;&nbsp;[Usage Examples](../../wiki/Usage)\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;[Extension Details](../../wiki/Extensions)\n" +
                "&nbsp;&nbsp;&nbsp;&nbsp;[Writing Extensions](../../wiki/Writing-Extensions)\n" +
                "\n" +
                "### Pegdown Migration Helper\n" +
                "\n" +
                "`PegdownOptionsAdapter` class converts pegdown `Extensions.*` flags to flexmark options and\n" +
                "extensions list. Pegdown `Extensions.java` is included for convenience and new options not found\n" +
                "in pegdown 1.6.0. These are located in `flexmark-profile-pegdown` module but you can grab the\n" +
                "source from this repo: [PegdownOptionsAdapter.java], [Extensions.java] and make your own\n" +
                "version, modified to your project's needs.\n" +
                "\n" +
                "You can pass your extension flags to static `PegdownOptionsAdapter.flexmarkOptions(int)` or you\n" +
                "can instantiate `PegdownOptionsAdapter` and use convenience methods to set, add and remove\n" +
                "extension flags. `PegdownOptionsAdapter.getFlexmarkOptions()` will return a fresh copy of\n" +
                "`DataHolder` every time with the options reflecting pegdown extension flags.\n" +
                "\n" +
                "```java\n" +
                "import com.vladsch.flexmark.html.HtmlRenderer;\n" +
                "import com.vladsch.flexmark.parser.Parser;\n" +
                "import com.vladsch.flexmark.profile.pegdown.Extensions;\n" +
                "import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;\n" +
                "import com.vladsch.flexmark.util.data.DataHolder;\n" +
                "\n" +
                "public class PegdownOptions {\n" +
                "     final private static DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(\n" +
                "            Extensions.ALL\n" +
                "    );\n" +
                "\n" +
                "    static final Parser PARSER = Parser.builder(OPTIONS).build();\n" +
                "    static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();\n" +
                "\n" +
                "    // use the PARSER to parse and RENDERER to render with pegdown compatibility\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "Default flexmark-java pegdown emulation uses less strict HTML block parsing which interrupts an\n" +
                "HTML block on a blank line. Pegdown only interrupts an HTML block on a blank line if all tags in\n" +
                "the HTML block are closed.\n" +
                "\n" +
                "To get closer to original pegdown HTML block parsing behavior use the method which takes a\n" +
                "`boolean strictHtml` argument:\n" +
                "\n" +
                "```java\n" +
                "import com.vladsch.flexmark.html.HtmlRenderer;\n" +
                "import com.vladsch.flexmark.parser.Parser;\n" +
                "import com.vladsch.flexmark.profile.pegdown.Extensions;\n" +
                "import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;\n" +
                "import com.vladsch.flexmark.util.data.DataHolder;\n" +
                "\n" +
                "public class PegdownOptions {\n" +
                "     final private static DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(true,\n" +
                "            Extensions.ALL\n" +
                "    );\n" +
                "\n" +
                "    static final Parser PARSER = Parser.builder(OPTIONS).build();\n" +
                "    static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();\n" +
                "\n" +
                "    // use the PARSER to parse and RENDERER to render with pegdown compatibility\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "A sample with a [custom link resolver] is also available, which includes link resolver for\n" +
                "changing URLs or attributes of links and a custom node renderer if you need to override the\n" +
                "generated link HTML.\n" +
                "\n" +
                ":information_source: [flexmark-java] has many more extensions and configuration options than\n" +
                "[pegdown] in addition to extensions available in pegdown 1.6.0.\n" +
                "[Available Extensions via PegdownOptionsAdapter](../../wiki/Pegdown-Migration#available-extensions-via-pegdownoptionsadapter)\n" +
                "\n" +
                "### Latest Additions and Changes\n" +
                "\n" +
                "* Major reorganization and code cleanup of implementation in version 0.60.0, see\n" +
                "  [Version-0.60.0-Changes](../../wiki/Version-0.60.0-Changes) thanks to great work by\n" +
                "  [Alex Karezin](mailto:javadiagrams@gmail.com) you can get an overview of module dependencies\n" +
                "  with ability to drill down to packages and classes.\n" +
                "* [Merge API](../../wiki/Markdown-Merge-API) to merge multiple markdown documents into a single\n" +
                "  document.\n" +
                "* Docx Renderer Extension:\n" +
                "  [Limited Attributes Node Handling](../../wiki/Docx-Renderer-Extension#limited-attributes-node-handling)\n" +
                "* Extensible HTML to Markdown Converter module:\n" +
                "  [flexmark-html2md-converter](flexmark-html2md-converter). Sample:\n" +
                "  [HtmlToMarkdownCustomizedSample.java]\n" +
                "* Java9+ module compatibility\n" +
                "* Compound Enumerated References\n" +
                "  [Enumerated References Extension](../../wiki/Enumerated-References-Extension) for creating\n" +
                "  legal numbering for elements and headings.\n" +
                "* [Macros Extension](../../wiki/Macros-Extension) to allow arbitrary markdown content to be\n" +
                "  inserted as block or inline elements, allowing block elements to be used where only inline\n" +
                "  elements are allowed by syntax.\n" +
                "* [GitLab Flavoured Markdown](../../wiki/Extensions#gitlab-flavoured-markdown) for parsing and\n" +
                "  rendering GitLab markdown extensions.\n" +
                "* OSGi module courtesy Dan Klco (GitHub [@klcodanr](https://github.com/klcodanr))\n" +
                "* [Media Tags](../../wiki/Extensions#media-tags) Media link transformer extension courtesy\n" +
                "  Cornelia Schultz (GitHub [@CorneliaXaos](https://github.com/CorneliaXaos)) transforms links\n" +
                "  using custom prefixes to Audio, Embed, Picture, and Video HTML5 tags.\n" +
                "* [Translation Helper API](../../wiki/Translation-Helper-API) to make translating markdown\n" +
                "  documents easier.\n" +
                "* [Admonition](../../wiki/Extensions#admonition) To create block-styled side content. For\n" +
                "  complete documentation please see the [Admonition Extension, Material for MkDocs]\n" +
                "  documentation.\n" +
                "* [Enumerated Reference](../../wiki/Extensions#enumerated-reference) to create enumerated\n" +
                "  references for figures, tables and other markdown elements.\n" +
                "* [Attributes](../../wiki/Extensions#attributes) to parse attributes of the form `{name\n" +
                "  name=value name='value' name=\"value\" #id .class-name}` attributes.\n" +
                "* [YouTube Embedded Link Transformer](../../wiki/Extensions#youtube-embedded-link-transformer)\n" +
                "  thanks to Vyacheslav N. Boyko (GitHub @bvn13) transforms simple links to youtube videos to\n" +
                "  embedded video iframe HTML.\n" +
                "* [Docx Converter](../../wiki/Extensions#docx-converter) using the [docx4j] library. How to use:\n" +
                "  [DocxConverter Sample], how to customize:\n" +
                "  [Customizing Docx Rendering](../../wiki/Customizing-Docx-Rendering)\n" +
                "\n" +
                "  Development of this module was sponsored by\n" +
                "  [Johner Institut GmbH](https://www.johner-institut.de).\n" +
                "* Update library to be [CommonMark (spec 0.28)] compliant and add\n" +
                "  `ParserEmulationProfile.COMMONMARK_0_27` and `ParserEmulationProfile.COMMONMARK_0_28` to allow\n" +
                "  selecting a specific spec version options.\n" +
                "* Custom node rendering API with ability to invoke standard rendering for an overridden node,\n" +
                "  allowing custom node renders that only handle special cases and let the rest be rendered as\n" +
                "  usual. [custom link resolver]\n" +
                "* [Gfm-Issues](../../wiki/Extensions#gfm-issues) and\n" +
                "  [Gfm-Users](../../wiki/Extensions#gfm-users) extensions for parsing and rendering `#123` and\n" +
                "  `@user-name` respectively.\n" +
                "* Deep HTML block parsing option for better handling of raw text tags that come after other tags\n" +
                "  and for [pegdown] HTML block parsing compatibility.\n" +
                "* `flexmark-all` module that includes: core, all extensions, formatter, JIRA and YouTrack\n" +
                "  converters, pegdown profile module and HTML to Markdown conversion.\n" +
                "* [PDF Output Module](../../wiki/Extensions#pdf-output-module)\n" +
                "  [PDF output](../../wiki/Usage#pdf-output) using [Open HTML To PDF]\n" +
                "* [Typographic](../../wiki/Extensions#typographic) implemented\n" +
                "* [XWiki Macro Extension](../../wiki/Extensions#xwiki-macro-extension)\n" +
                "* [Jekyll Tags](../../wiki/Extensions#jekyll-tags)\n" +
                "* [Html To Markdown](../../wiki/Extensions#html-to-markdown)\n" +
                "* [Maven Markdown Page Generator Plugin](https://github.com/vsch/markdown-page-generator-plugin)\n" +
                "* [Markdown Formatter](../../wiki/Markdown-Formatter) module to output AST as markdown with\n" +
                "  formatting options.\n" +
                "* [Tables](../../wiki/Extensions#tables) for [Markdown Formatter](../../wiki/Markdown-Formatter)\n" +
                "  with column width and alignment of markdown tables:\n" +
                "\n" +
                "  <table>\n" +
                "      <thead> <tr><th>Input</th> <th>Output</th> </tr> </thead>\n" +
                "      <tr><td>\n" +
                "      <pre><code class=\"language-markdown\">day|time|spent\n" +
                "  :---|:---:|--:\n" +
                "  nov. 2. tue|10:00|4h 40m\n" +
                "  nov. 3. thu|11:00|4h\n" +
                "  nov. 7. mon|10:20|4h 20m\n" +
                "  total:|| 13h</code></pre>\n" +
                "      </td><td>\n" +
                "      <pre><code class=\"language-markdown\">| day         | time  |   spent |\n" +
                "  |:------------|:-----:|--------:|\n" +
                "  | nov. 2. tue | 10:00 |  4h 40m |\n" +
                "  | nov. 3. thu | 11:00 |      4h |\n" +
                "  | nov. 7. mon | 10:20 |  4h 20m |\n" +
                "  | total:             ||     13h |</code></pre>\n" +
                "      </td></tr>\n" +
                "  </table>\n" +
                "\n" +
                "### Releases, Bug Fixes, Enhancements and Support\n" +
                "\n" +
                "I use flexmark-java as the parser for [Markdown Navigator] plugin for JetBrains IDEs. I tend to\n" +
                "use the latest, unreleased version to fix bugs or get improvements. So if you find a bug that is\n" +
                "a show stopper for your project or see a bug in [github issues page] marked `fixed for next\n" +
                "release` that is affecting your project then please let me know and I may be able to promptly\n" +
                "make a new release to address your issue. Otherwise, I will let bug fixes and enhancements\n" +
                "accumulate thinking no one is affected by what is already fixed.\n" +
                "\n" +
                "#### Extension points in the API are many and numerous\n" +
                "\n" +
                "There are many extension options in the API with their intended use. A good soft-start is the\n" +
                "[`flexmark-java-samples`](flexmark-java-samples) module which has simple samples for asked for\n" +
                "extensions. The next best place is the source of an existing extension that has similar syntax\n" +
                "to what you want to add.\n" +
                "\n" +
                "If your extension lines up with the right API, the task is usually very short and sweet. If your\n" +
                "extension uses the API in an unintended fashion or does not follow expected housekeeping\n" +
                "protocols, you may find it an uphill battle with a rat's nest of if/else condition handling and\n" +
                "fixing one bug only leading to creating another one.\n" +
                "\n" +
                "Generally, if it takes more than a few dozen lines to add a simple extension, then either you\n" +
                "are going about it wrong or the API is missing an extension point. If you look at all the\n" +
                "implemented extensions you will see that most are a few lines of code other than boiler plate\n" +
                "dictated by the API. That is the goal for this library: provide an extensible core that makes\n" +
                "writing extensions a breeze.\n" +
                "\n" +
                "The larger extensions are `flexmark-ext-tables` and `flexmark-ext-spec-example`, the meat of\n" +
                "both is around 200 lines of code. You can use them as a guide post for size estimating your\n" +
                "extension.\n" +
                "\n" +
                "My own experience adding extensions shows that sometimes a new type of extension is best\n" +
                "addressed with an API enhancement to make its implementation seamless, or by fixing a bug that\n" +
                "was not visible before the extension stressed the API in just the right way. Your intended\n" +
                "extension may just be the one requiring such an approach.\n" +
                "\n" +
                "#### Don't hesitate to open an issue if you can't find the answer\n" +
                "\n" +
                "The takeaway is: if you want to implement an extension or a feature please don't hesitate to\n" +
                "open an issue and I will give you pointers on the best way to go about it. It may save you a lot\n" +
                "of time by letting me improve the API to address your extension's needs before you put a lot of\n" +
                "fruitless effort into it.\n" +
                "\n" +
                "I do ask that you realize that I am chief cook and bottle washer on this project, without an\n" +
                "iota of Vulcan Mind Melding skills. I do ask that you describe what you want to implement\n" +
                "because I can't read your mind. Please do some reconnaissance background work around the source\n" +
                "code and documentation because I cannot transfer what I know to you, without your willing\n" +
                "effort.\n" +
                "\n" +
                "#### Consulting is available\n" +
                "\n" +
                "If you have a commercial application and don't want to write the extension(s) yourself or want\n" +
                "to reduce the time and effort of implementing extensions and integrating flexmark-java, feel\n" +
                "free to contact me. I am available on a consulting/contracting basis.\n" +
                "\n" +
                "### Markdown Processor Emulation\n" +
                "\n" +
                "Despite its name, commonmark is neither a superset nor a subset of other markdown flavors.\n" +
                "Rather, it proposes a standard, unambiguous syntax specification for the original, \"core\"\n" +
                "Markdown, thus effectively introducing yet another flavor. While flexmark is by default\n" +
                "commonmark compliant, its parser can be tweaked in various ways. The sets of tweaks required to\n" +
                "emulate the most commonly used markdown parsers around are available in flexmark as\n" +
                "`ParserEmulationProfiles`.\n" +
                "\n" +
                "As the name `ParserEmulationProfile` implies, it's only the parser that is adjusted to the\n" +
                "specific markdown flavor. Applying the profile does not add features beyond those available in\n" +
                "commonmark. If you want to use flexmark to fully emulate another markdown processor's behavior,\n" +
                "you have to adjust the parser and configure the flexmark extensions that provide the additional\n" +
                "features available in the parser that you want to emulate.\n" +
                "\n" +
                "A rewrite of the list parser to better control emulation of other markdown processors as per\n" +
                "[Markdown Processors Emulation](MarkdownProcessorsEmulation.md) is complete. Addition of\n" +
                "processor presets to emulate specific markdown processing behaviour of these parsers is on a\n" +
                "short to do list.\n" +
                "\n" +
                "Some emulation families do a better better job of emulating their target than others. Most of\n" +
                "the effort was directed at emulating how these processors parse standard Markdown and list\n" +
                "related parsing specifically. For processors that extend original Markdown, you will need to add\n" +
                "those extensions that are already implemented in flexmark-java to the Parser/Renderer builder\n" +
                "options.\n" +
                "\n" +
                "Extensions will be modified to include their own presets for specific processor emulation, if\n" +
                "that processor has an equivalent extension implemented.\n" +
                "\n" +
                "If you find a discrepancy please open an issue so it can be addressed.\n" +
                "\n" +
                "Major processor families are implemented and some family members also:\n" +
                "\n" +
                "* [ ] [Jekyll]\n" +
                "* [CommonMark] for latest implemented spec, currently [CommonMark (spec 0.28)]\n" +
                "  * [ ] [League/CommonMark]\n" +
                "  * [CommonMark (spec 0.27)] for specific version compatibility\n" +
                "  * [CommonMark (spec 0.28)] for specific version compatibility\n" +
                "  * [GitHub] Comments\n" +
                "* [Markdown.pl][Markdown]\n" +
                "  * [ ] [Php Markdown Extra]\n" +
                "  * [GitHub] Docs (old GitHub markdown parser)\n" +
                "* [Kramdown]\n" +
                "* FixedIndent\n" +
                "  * [MultiMarkdown]\n" +
                "  * [Pegdown]\n" +
                "\n" +
                ":information_source: profiles to encapsulate configuration details for variants within the\n" +
                "family were added in 0.11.0:\n" +
                "\n" +
                "* CommonMark (default for family): `ParserEmulationProfile.COMMONMARK`\n" +
                "* FixedIndent (default for family): `ParserEmulationProfile.FIXED_INDENT`\n" +
                "* GitHub Comments (just CommonMark): `ParserEmulationProfile.COMMONMARK`\n" +
                "* Old GitHub Docs: `ParserEmulationProfile.GITHUB_DOC`\n" +
                "* Kramdown (default for family): `ParserEmulationProfile.KRAMDOWN`\n" +
                "* Markdown.pl (default for family): `ParserEmulationProfile.MARKDOWN`\n" +
                "* MultiMarkdown: `ParserEmulationProfile.MULTI_MARKDOWN`\n" +
                "* Pegdown, with pegdown extensions use `PegdownOptionsAdapter` in `flexmark-profile-pegdown`\n" +
                "* Pegdown, without pegdown extensions `ParserEmulationProfile.PEGDOWN`\n" +
                "* Pegdown HTML block parsing rules, without pegdown extensions\n" +
                "  `ParserEmulationProfile.PEGDOWN_STRICT`\n" +
                "\n" +
                "### History and Motivation\n" +
                "\n" +
                "**flexmark-java** is a fork of [commonmark-java] project, modified to generate an AST which\n" +
                "reflects all the elements in the original source, full source position tracking for all elements\n" +
                "in the AST and easier JetBrains Open API PsiTree generation.\n" +
                "\n" +
                "The API was changed to allow more granular control of the parsing process and optimized for\n" +
                "parsing with a large number of installed extensions. The parser and extensions come with many\n" +
                "tweaking options for parser behavior and HTML rendering variations. The end goal is to have the\n" +
                "parser and renderer be able to mimic other parsers with great degree of accuracy.\n" +
                "\n" +
                "Motivation for this was the need to replace [pegdown] parser in [Markdown Navigator] plugin.\n" +
                "[pegdown] has a great feature set but its speed in general is less than ideal and for\n" +
                "pathological input either hangs or practically hangs during parsing.\n" +
                "\n" +
                "[commonmark-java] has an excellent parsing architecture that is easy to understand and extend.\n" +
                "The goal was to ensure that adding source position tracking in the AST would not change the ease\n" +
                "of parsing and generating the AST more than absolutely necessary.\n" +
                "\n" +
                "Reasons for choosing [commonmark-java] as the parser are: speed, ease of understanding, ease of\n" +
                "extending and speed. Now that I have reworked the core and added a few extensions I am extremely\n" +
                "satisfied with my choice.\n" +
                "\n" +
                "Another goal was to improve the ability of extensions to modify parser behavior so that any\n" +
                "dialect of markdown could be implemented through the extension mechanism. An extensible options\n" +
                "API was added to allow setting of all options in one place. Parser, renderer and extensions use\n" +
                "these options for configuration, including disabling some core block parsers.\n" +
                "\n" +
                "This is a work in progress with many API changes. No attempt is made to keep backward API\n" +
                "compatibility to the original project and until the feature set is mostly complete, not even to\n" +
                "earlier versions of this project.\n" +
                "\n" +
                "### Feature Comparison\n" +
                "\n" +
                "| Feature                                                                          | flexmark-java                                                    | commonmark-java                                                   | pegdown                                                              |\n" +
                "|:---------------------------------------------------------------------------------|:-----------------------------------------------------------------|:------------------------------------------------------------------|:---------------------------------------------------------------------|\n" +
                "| Relative parse time (less is better)                                             | :heavy_check_mark: 1x [(1)](#1)                                  | :heavy_check_mark: 0.6x to 0.7x [(2)](#2)                         | :x: 25x average, 20,000x to ∞ for pathological input [(3)](#3)       |\n" +
                "| All source elements in the AST                                                   | :heavy_check_mark:                                               | :x:                                                               | :heavy_check_mark:                                                   |\n" +
                "| AST elements with source position                                                | :heavy_check_mark:                                               | :heavy_check_mark:                                                | :heavy_check_mark: with some errors and idiosyncrasies               |\n" +
                "| AST can be easily manipulated                                                    | :heavy_check_mark: AST post processing is an extension mechanism | :heavy_check_mark: AST post processing is an extension mechanism  | :x: not an option. No node's parent information, children as List<>. |\n" +
                "| AST elements have detailed source position for all parts                         | :heavy_check_mark:                                               | :x:                                                               | :x: only node start/end                                              |\n" +
                "| Can disable core parsing features                                                | :heavy_check_mark:                                               | :x:                                                               | :x:                                                                  |\n" +
                "| Core parser implemented via the extension API                                    | :heavy_check_mark:                                               | :x: `instanceOf` tests for specific block parser and node classes | :x: core exposes few extension points                                |\n" +
                "| Easy to understand and modify parser implementation                              | :heavy_check_mark:                                               | :heavy_check_mark:                                                | :x: one PEG parser with complex interactions [(3)](#3)               |\n" +
                "| Parsing of block elements is independent from each other                         | :heavy_check_mark:                                               | :heavy_check_mark:                                                | :x: everything in one PEG grammar                                    |\n" +
                "| Uniform configuration across: parser, renderer and all extensions                | :heavy_check_mark:                                               | :x: none beyond extension list                                    | :x: `int` bit flags for core, none for extensions                    |\n" +
                "| Parsing performance optimized for use with extensions                            | :heavy_check_mark:                                               | :x: parsing performance for core, extensions do what they can     | :x: performance is not a feature                                     |\n" +
                "| Feature rich with many configuration options and extensions out of the box       | :heavy_check_mark:                                               | :x: limited extensions, no options                                | :heavy_check_mark:                                                   |\n" +
                "| Dependency definitions for processors to guarantee the right order of processing | :heavy_check_mark:                                               | :x: order specified by extension list ordering, error prone       | :x: not applicable, core defines where extension processing is added |\n" +
                "\n" +
                "<!--\n" +
                "|  | :x: | :x: | :x: |\n" +
                "|  | :x: | :x: | :x: |\n" +
                "|  | :x: | :x: | :x: |\n" +
                "|  | :x: | :x: | :x: |\n" +
                "|  | :x: | :x: | :x: |\n" +
                "|--|:----|:----|:----|\n" +
                "|  | :x: | :x: | :x: |\n" +
                "-->\n" +
                "\n" +
                "###### (1)\n" +
                "\n" +
                "flexmark-java pathological input of 100,000 `[` parses in 68ms, 100,000 `]` in 57ms, 100,000\n" +
                "nested `[` `]` parse in 55ms\n" +
                "\n" +
                "###### (2)\n" +
                "\n" +
                "commonmark-java pathological input of 100,000 `[` parses in 30ms, 100,000 `]` in 30ms, 100,000\n" +
                "nested `[` `]` parse in 43ms\n" +
                "\n" +
                "###### (3)\n" +
                "\n" +
                "pegdown pathological input of 17 `[` parses in 650ms, 18 `[` in 1300ms\n" +
                "\n" +
                "Progress\n" +
                "--------\n" +
                "\n" +
                "* Parser options, items marked as a task item are to be implemented the rest are complete:\n" +
                "  * Typographic\n" +
                "    * Quotes\n" +
                "    * Smarts\n" +
                "  * GitHub Extensions\n" +
                "    * Fenced code blocks\n" +
                "    * Anchor links for headers with auto id generation\n" +
                "    * Table Spans option to be implemented for tables extension\n" +
                "    * Wiki Links with GitHub and Creole syntax\n" +
                "    * Emoji Shortcuts with use GitHub emoji URL option\n" +
                "  * GitHub Syntax\n" +
                "    * Strikethrough\n" +
                "    * Task Lists\n" +
                "    * No Atx Header Space\n" +
                "    * No Header indents\n" +
                "    * Hard Wraps (achieved with SOFT_BREAK option changed to `\"<br />\"`)\n" +
                "    * Relaxed HR Rules Option\n" +
                "    * Wiki links\n" +
                "  * Publishing\n" +
                "    * Abbreviations\n" +
                "    * Footnotes\n" +
                "    * Definitions\n" +
                "    * Table of Contents\n" +
                "  * Suppress\n" +
                "    * inline HTML: all, non-comments, comments\n" +
                "    * HTML blocks: all, non-comments, comments\n" +
                "  * Processor Extensions\n" +
                "    * Jekyll front matter\n" +
                "    * Jekyll tag elements, with support for `{% include file %}`,\n" +
                "      [Include Markdown and HTML File Content]\n" +
                "    * GitBook link URL encoding. Not applicable\n" +
                "    * HTML comment nodes: Block and Inline\n" +
                "    * Multi-line Image URLs\n" +
                "    * Spec Example Element\n" +
                "  * Commonmark Syntax suppression\n" +
                "    * Manual loose lists\n" +
                "    * Numbered lists always start with 1.\n" +
                "    * Fixed list item indent, items must be indented by at least 4 spaces\n" +
                "    * Relaxed list start option, allow lists to start when not preceded by a blank line.\n" +
                "\n" +
                "I am very pleased with the decision to switch to [commonmark-java] based parser for my own\n" +
                "projects. Even though I had to do major surgery on its innards to get full source position\n" +
                "tracking and AST that matches source elements, it is a pleasure to work with and is now a\n" +
                "pleasure to extend. If you don't need source level element AST or the rest of what flexmark-java\n" +
                "added and [CommonMark] is your target markdown parser then I encourage you to use\n" +
                "[commonmark-java] as it is an excellent choice for your needs and its performance does not\n" +
                "suffer for the overhead of features that you will not use.\n" +
                "\n" +
                "Benchmarks\n" +
                "----------\n" +
                "\n" +
                "Latest, Jan 28, 2017 flexmark-java 0.13.1, intellij-markdown from CE EAP 2017, commonmark-java\n" +
                "0.8.0:\n" +
                "\n" +
                "| File             | commonmark-java | flexmark-java | intellij-markdown |   pegdown |\n" +
                "|:-----------------|----------------:|--------------:|------------------:|----------:|\n" +
                "| README-SLOW      |         0.420ms |       0.812ms |           2.027ms |  15.483ms |\n" +
                "| VERSION          |         0.743ms |       1.425ms |           4.057ms |  42.936ms |\n" +
                "| commonMarkSpec   |        31.025ms |      44.465ms |         600.654ms | 575.131ms |\n" +
                "| markdown_example |         8.490ms |      10.502ms |         223.593ms | 983.640ms |\n" +
                "| spec             |         4.719ms |       6.249ms |          35.883ms | 307.176ms |\n" +
                "| table            |         0.229ms |       0.623ms |           0.800ms |   3.642ms |\n" +
                "| table-format     |         1.385ms |       2.881ms |           4.150ms |  23.592ms |\n" +
                "| wrap             |         3.804ms |       4.589ms |          16.609ms |  86.383ms |\n" +
                "\n" +
                "Ratios of above:\n" +
                "\n" +
                "| File             | commonmark-java | flexmark-java | intellij-markdown |   pegdown |\n" +
                "|:-----------------|----------------:|--------------:|------------------:|----------:|\n" +
                "| README-SLOW      |            1.00 |          1.93 |              4.83 |     36.88 |\n" +
                "| VERSION          |            1.00 |          1.92 |              5.46 |     57.78 |\n" +
                "| commonMarkSpec   |            1.00 |          1.43 |             19.36 |     18.54 |\n" +
                "| markdown_example |            1.00 |          1.24 |             26.34 |    115.86 |\n" +
                "| spec             |            1.00 |          1.32 |              7.60 |     65.09 |\n" +
                "| table            |            1.00 |          2.72 |              3.49 |     15.90 |\n" +
                "| table-format     |            1.00 |          2.08 |              3.00 |     17.03 |\n" +
                "| wrap             |            1.00 |          1.21 |              4.37 |     22.71 |\n" +
                "| **overall**      |        **1.00** |      **1.41** |         **17.47** | **40.11** |\n" +
                "\n" +
                "| File             | commonmark-java | flexmark-java | intellij-markdown |   pegdown |\n" +
                "|:-----------------|----------------:|--------------:|------------------:|----------:|\n" +
                "| README-SLOW      |            0.52 |          1.00 |              2.50 |     19.07 |\n" +
                "| VERSION          |            0.52 |          1.00 |              2.85 |     30.12 |\n" +
                "| commonMarkSpec   |            0.70 |          1.00 |             13.51 |     12.93 |\n" +
                "| markdown_example |            0.81 |          1.00 |             21.29 |     93.66 |\n" +
                "| spec             |            0.76 |          1.00 |              5.74 |     49.15 |\n" +
                "| table            |            0.37 |          1.00 |              1.28 |      5.85 |\n" +
                "| table-format     |            0.48 |          1.00 |              1.44 |      8.19 |\n" +
                "| wrap             |            0.83 |          1.00 |              3.62 |     18.83 |\n" +
                "| **overall**      |        **0.71** |      **1.00** |         **12.41** | **28.48** |\n" +
                "\n" +
                "---\n" +
                "\n" +
                "Because these two files represent the pathological input for pegdown, I no longer run them as\n" +
                "part of the benchmark to prevent skewing of the results. The results are here for posterity.\n" +
                "\n" +
                "| File          | commonmark-java | flexmark-java | intellij-markdown |    pegdown |\n" +
                "|:--------------|----------------:|--------------:|------------------:|-----------:|\n" +
                "| hang-pegdown  |         0.082ms |       0.326ms |           0.342ms |  659.138ms |\n" +
                "| hang-pegdown2 |         0.048ms |       0.235ms |           0.198ms | 1312.944ms |\n" +
                "\n" +
                "Ratios of above:\n" +
                "\n" +
                "| File          | commonmark-java | flexmark-java | intellij-markdown |      pegdown |\n" +
                "|:--------------|----------------:|--------------:|------------------:|-------------:|\n" +
                "| hang-pegdown  |            1.00 |          3.98 |              4.17 |      8048.38 |\n" +
                "| hang-pegdown2 |            1.00 |          4.86 |              4.10 |     27207.32 |\n" +
                "| **overall**   |        **1.00** |      **4.30** |          **4.15** | **15151.91** |\n" +
                "\n" +
                "| File          | commonmark-java | flexmark-java | intellij-markdown |     pegdown |\n" +
                "|:--------------|----------------:|--------------:|------------------:|------------:|\n" +
                "| hang-pegdown  |            0.25 |          1.00 |              1.05 |     2024.27 |\n" +
                "| hang-pegdown2 |            0.21 |          1.00 |              0.84 |     5594.73 |\n" +
                "| **overall**   |        **0.23** |      **1.00** |          **0.96** | **3519.73** |\n" +
                "\n" +
                "* [VERSION.md] is the version log file I use for Markdown Navigator\n" +
                "* [commonMarkSpec.md] is a 33k line file used in [intellij-markdown] test suite for performance\n" +
                "  evaluation.\n" +
                "* [spec.txt] commonmark spec markdown file in the [commonmark-java] project\n" +
                "* [hang-pegdown.md] is a file containing a single line of 17 characters `[[[[[[[[[[[[[[[[[`\n" +
                "  which causes pegdown to go into a hyper-exponential parse time.\n" +
                "* [hang-pegdown2.md] a file containing a single line of 18 characters `[[[[[[[[[[[[[[[[[[` which\n" +
                "  causes pegdown to go into a hyper-exponential parse time.\n" +
                "* [wrap.md] is a file I was using to test wrap on typing performance only to discover that it\n" +
                "  has nothing to do with the wrap on typing code when 0.1 seconds is taken by pegdown to parse\n" +
                "  the file. In the plugin the parsing may happen more than once: syntax highlighter pass, psi\n" +
                "  tree building pass, external annotator.\n" +
                "* markdown_example.md a file with 10,000+ lines containing 500kB+ of text.\n" +
                "\n" +
                "Contributing\n" +
                "------------\n" +
                "\n" +
                "Pull requests, issues and comments welcome :smile:. For pull requests:\n" +
                "\n" +
                "* Add tests for new features and bug fixes, preferably in the\n" +
                "  [ast_spec.md](flexmark-core-test/src/test/resources/ast_spec.md) format\n" +
                "* Follow the existing style to make merging easier, as much as possible: 4 space indent,\n" +
                "  trailing spaces trimmed.\n" +
                "\n" +
                "* * *\n" +
                "\n" +
                "License\n" +
                "-------\n" +
                "\n" +
                "Copyright (c) 2015-2016 Atlassian and others.\n" +
                "\n" +
                "Copyright (c) 2016-2023, Vladimir Schneider,\n" +
                "\n" +
                "BSD (2-clause) licensed, see [LICENSE.txt] file.\n" +
                "\n" +
                "[Admonition Extension, Material for MkDocs]: https://squidfunk.github.io/mkdocs-material/reference/admonitions/\n" +
                "[CommonMark]: https://commonmark.org\n" +
                "[CommonMark (spec 0.27)]: https://spec.commonmark.org/0.27\n" +
                "[CommonMark (spec 0.28)]: https://spec.commonmark.org/0.28\n" +
                "[DocxConverter Sample]: flexmark-java-samples/src/com/vladsch/flexmark/java/samples/DocxConverterCommonMark.java\n" +
                "[Extensions.java]: flexmark-profile-pegdown/src/main/java/com/vladsch/flexmark/profile/pegdown/Extensions.java\n" +
                "[GitHub]: https://github.com/vsch/laravel-translation-manager\n" +
                "[GitHub Issues page]: ../../issues\n" +
                "[HtmlToMarkdownCustomizedSample.java]: flexmark-java-samples/src/com/vladsch/flexmark/java/samples/HtmlToMarkdownCustomizedSample.java\n" +
                "[Include Markdown and HTML File Content]: ../../wiki/Usage#include-markdown-and-html-file-content\n" +
                "[Jekyll]: https://jekyllrb.com\n" +
                "[Kramdown]: https://kramdown.gettalong.org\n" +
                "[LICENSE.txt]: LICENSE.txt\n" +
                "[League/CommonMark]: https://github.com/thephpleague/commonmark\n" +
                "[Markdown]: https://daringfireball.net/projects/markdown/\n" +
                "[Markdown Navigator]: https://github.com/vsch/idea-multimarkdown\n" +
                "[MultiMarkdown]: https://fletcherpenney.net/multimarkdown\n" +
                "[Open HTML To PDF]: https://github.com/danfickle/openhtmltopdf\n" +
                "[PHP Markdown Extra]: https://michelf.ca/projects/php-markdown/extra/#abbr\n" +
                "[PegdownOptionsAdapter.java]: flexmark-profile-pegdown/src/main/java/com/vladsch/flexmark/profile/pegdown/PegdownOptionsAdapter.java\n" +
                "[VERSION.md]: https://github.com/vsch/idea-multimarkdown/blob/master/test-data/performance/VERSION.md\n" +
                "[commonmark-java]: https://github.com/atlassian/commonmark-java\n" +
                "[commonMarkSpec.md]: https://github.com/vsch/idea-multimarkdown/blob/master/test-data/performance/commonMarkSpec.md\n" +
                "[custom link resolver]: flexmark-java-samples/src/com/vladsch/flexmark/java/samples/PegdownCustomLinkResolverOptions.java\n" +
                "[docx4j]: https://www.docx4java.org/trac/docx4j\n" +
                "[flexmark-java]: https://github.com/vsch/flexmark-java\n" +
                "[hang-pegdown.md]: https://github.com/vsch/idea-multimarkdown/blob/master/test-data/performance/hang-pegdown.md\n" +
                "[hang-pegdown2.md]: https://github.com/vsch/idea-multimarkdown/blob/master/test-data/performance/hang-pegdown2.md\n" +
                "[intellij-markdown]: https://github.com/valich/intellij-markdown\n" +
                "[pegdown]: https://github.com/sirthias/pegdown\n" +
                "[spec.txt]: https://github.com/vsch/idea-multimarkdown/blob/master/test-data/performance/spec.md\n" +
                "[wrap.md]: https://github.com/vsch/idea-multimarkdown/blob/master/test-data/performance/wrap.md\n";


        q = MarkdownUtil.extractText(q);
        for (int i = 0; i <= q.length() / 1000; i++) {
            String s = q.substring(i * 1000, Math.min(q.length(), (i + 1) * 1000));
            messages.add(new Message("user", s));
        }
        messages.add(new Message("system", "将用户输入的内容总结后翻译成中文"));

        String answer = gpt.chat(messages);
        System.out.println(answer);
    }
}
