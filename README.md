<h1 id="altairkit">AltairKit</h1>
<p>This project is meant to simplify Minecraft plugin development.</p>
<h2 id="installation">Installation</h2>
<ol type="1">
	<li>
		Download the
		<code>AltairKit.jar</code>
		file and the
		<code>AltairBuilder.exe</code>
		file.
	</li>
	<li>
		Place both files in the same folder and run
		<code>AltairBuilder.exe</code>
		.
	</li>
	<li>Add the dependency to your project:</li>
</ol>
<h3>Dependency</h3>
<h3>Maven</h3>
<div class="sourceCode" id="cb1">
	<pre class="sourceCode xml"><code class="sourceCode xml">&lt;dependency&gt;
  &lt;groupId&gt;dev.mikan&lt;/groupId&gt;
  &lt;artifactId&gt;AltairKit&lt;/artifactId&gt;
  &lt;version&gt;${version}&lt;/version&gt;
&lt;/dependency&gt;
</code></pre>
</div>
<h3 id="gradle.kts">Gradle .kts</h3>
<div class="sourceCode" id="cb2">
	<pre class="sourceCode kt"><code class="sourceCode kotlin">implementation("dev.mikan:AltairKit:1.21.4")
</code></pre>
</div>

<h2 id="commands">Commands</h2>
					<p>
						Commands API allows to create commands in the easiest way, by using
						<code>annotations</code>
						.
					</p>
					<p>
						In order to successgully register it, the command method must be
						wrapped in a class which implements CmdClass and have at least the
						<code>@Command("")</code>
						annotation and an Actor object as the
						<code>first</code>
						parameter.
					</p>
					<h3 id="command-example">Command example:</h3>
					<div class="sourceCode" id="cb1">
						<pre
							class="sourceCode java"
						><code class="sourceCode java"><span id="cb1-1"><a href="#cb1-1" aria-hidden="true" tabindex="-1"></a><span class="kw">public</span> <span class="kw">class</span> CmdTest <span class="kw">implements</span> CmdClass <span class="op">{</span></span>
<span id="cb1-2"><a href="#cb1-2" aria-hidden="true" tabindex="-1"></a></span>
<span id="cb1-3"><a href="#cb1-3" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Command</span><span class="op">(</span><span class="st">&quot;Altair&quot;</span><span class="op">)</span></span>
<span id="cb1-4"><a href="#cb1-4" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Complete</span><span class="op">({</span><span class="st">&quot;kit&quot;</span><span class="op">,</span><span class="st">&quot;by&quot;</span><span class="op">,</span><span class="st">&quot;mikan&quot;</span><span class="op">})</span></span>
<span id="cb1-5"><a href="#cb1-5" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Permission</span><span class="op">(</span><span class="st">&quot;dev.mikan.module&quot;</span><span class="op">)</span></span>
<span id="cb1-6"><a href="#cb1-6" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Sender</span><span class="op">(</span>User<span class="op">.</span><span class="fu">PLAYER</span><span class="op">)</span></span>
<span id="cb1-7"><a href="#cb1-7" aria-hidden="true" tabindex="-1"></a>  <span class="kw">public</span> <span class="dt">void</span> <span class="fu">altair</span><span class="op">(</span><span class="dt">final</span> Actor actor<span class="op">)</span> <span class="op">{</span></span>
<span id="cb1-8"><a href="#cb1-8" aria-hidden="true" tabindex="-1"></a>    actor<span class="op">.</span><span class="fu">reply</span><span class="op">(</span><span class="st">&quot;&lt;green&gt;Thank you for using altair kit!&quot;</span><span class="op">);</span></span>
<span id="cb1-9"><a href="#cb1-9" aria-hidden="true" tabindex="-1"></a>  <span class="op">}</span></span>
<span id="cb1-10"><a href="#cb1-10" aria-hidden="true" tabindex="-1"></a><span class="op">}</span></span></code></pre>
					</div>
					<p>
						Once you created a class with methods like this you can register the
						actual command by calling
					</p>
					<div class="sourceCode" id="cb2">
						<pre
							class="sourceCode java"
						><code class="sourceCode java"><span id="cb2-1"><a href="#cb2-1" aria-hidden="true" tabindex="-1"></a>AltairKit<span class="op">.</span><span class="fu">registerCommands</span><span class="op">(</span>cmdClass<span class="op">)</span></span></code></pre>
					</div>
					<h3 id="annotations">Annotations</h3>
					<p>
						<code>@Command("")</code>
						Is used to define the command, you can write a sub command by
						spacing 1 char, this root command and its subcommands will
						automatically be tab-completed.
					</p>
					<p>
						<code>@Complete(array)</code>
						Is used to add an additional tab complete on the last subcommand.
					</p>
					<p>
						<code>@Permission("",blocking)</code>
						Self explainatory, the blocking param is a boolean (default true)
						which if it’s set on false even though the player do not have the
						permission the mathod will be called.
					</p>
					<p>
						<code>@Sender(SenderType)</code>
						Block the command if the sender type does not match the specified
						one.
					</p>
					<h3 id="actor">Actor</h3>
					<p>
						It is the one who performed the command, the object has some method
						you can use like:
					</p>
					<p>
						<code>reply("&lt;color&gt;Message");</code>
						return a message to the command sender (automatically colorize it)
					</p>
					<p>
						<code>asPlayer()</code>
						cast sender to player, returns null if sender is not player
					</p>
					<p>
						<code>asConsole()</code>
						cast sender to console, returns null if sender is not console
					</p>
					<h3 id="parameters">Parameters</h3>
					<p>It is possible to expect as many parameters as you want:</p>
					<div class="sourceCode" id="cb3">
						<pre
							class="sourceCode java"
						><code class="sourceCode java"><span id="cb3-1"><a href="#cb3-1" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Command</span><span class="op">(</span><span class="st">&quot;Altair&quot;</span><span class="op">)</span></span>
<span id="cb3-2"><a href="#cb3-2" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Complete</span><span class="op">({</span><span class="st">&quot;kit&quot;</span><span class="op">,</span><span class="st">&quot;by&quot;</span><span class="op">,</span><span class="st">&quot;mikan&quot;</span><span class="op">})</span></span>
<span id="cb3-3"><a href="#cb3-3" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Permission</span><span class="op">(</span><span class="st">&quot;dev.mikan.module&quot;</span><span class="op">)</span></span>
<span id="cb3-4"><a href="#cb3-4" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Sender</span><span class="op">(</span>User<span class="op">.</span><span class="fu">PLAYER</span><span class="op">)</span></span>
<span id="cb3-5"><a href="#cb3-5" aria-hidden="true" tabindex="-1"></a>  <span class="kw">public</span> <span class="dt">void</span> <span class="fu">altair</span><span class="op">(</span><span class="dt">final</span> Actor actor<span class="op">,</span><span class="dt">double</span> number<span class="op">,</span> <span class="bu">String</span> message<span class="op">)</span> <span class="op">{</span></span>
<span id="cb3-6"><a href="#cb3-6" aria-hidden="true" tabindex="-1"></a>    actor<span class="op">.</span><span class="fu">reply</span><span class="op">(</span><span class="st">&quot;&lt;green&gt;Thank you for using altair kit!&quot;</span><span class="op">);</span></span>
<span id="cb3-7"><a href="#cb3-7" aria-hidden="true" tabindex="-1"></a>  <span class="op">}</span></span></code></pre>
					</div>
					<p>
						However when those are not passed it will return those values: 
					</p>
					<or>
						<li>Player: null</li>
						<li>int: -1</li>
						<li>double: -1.0</li>
						<li>String: “”</li>
					</or>
					<h3 id="default-annotation">Default annotation</h3>
					<div class="sourceCode" id="cb4">
						<pre
							class="sourceCode java"
						><code class="sourceCode java"><span id="cb4-1"><a href="#cb4-1" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Command</span><span class="op">(</span><span class="st">&quot;Altair&quot;</span><span class="op">)</span></span>
<span id="cb4-2"><a href="#cb4-2" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Complete</span><span class="op">({</span><span class="st">&quot;kit&quot;</span><span class="op">,</span><span class="st">&quot;by&quot;</span><span class="op">,</span><span class="st">&quot;mikan&quot;</span><span class="op">})</span></span>
<span id="cb4-3"><a href="#cb4-3" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Permission</span><span class="op">(</span><span class="st">&quot;dev.mikan.module&quot;</span><span class="op">)</span></span>
<span id="cb4-4"><a href="#cb4-4" aria-hidden="true" tabindex="-1"></a>  <span class="at">@Sender</span><span class="op">(</span>User<span class="op">.</span><span class="fu">PLAYER</span><span class="op">)</span></span>
<span id="cb4-5"><a href="#cb4-5" aria-hidden="true" tabindex="-1"></a>  <span class="kw">public</span> <span class="dt">void</span> <span class="fu">altair</span><span class="op">(</span><span class="dt">final</span> Actor actor<span class="op">,</span> <span class="at">@Default</span> Player target<span class="op">,</span> <span class="bu">String</span> message<span class="op">)</span> <span class="op">{</span></span>
<span id="cb4-6"><a href="#cb4-6" aria-hidden="true" tabindex="-1"></a>    target<span class="op">.</span><span class="fu">sendMessage</span><span class="op">(</span>message<span class="op">);</span></span>
<span id="cb4-7"><a href="#cb4-7" aria-hidden="true" tabindex="-1"></a>  <span class="op">}</span></span></code></pre>
					</div>
					<p>
						In this situation if the player is passed as parameter, then it will
						behave as normal; if it’s not, and the command is called like:
						/altair Hi! , then target will be filled out with sender instance
					</p>
