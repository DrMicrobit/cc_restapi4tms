Max: Cline is allowed tool.  Reprio Cline to P1
# TODO
+ [/] Max: Cline is allowed tool.  Reprio Cline to P1. Verify Cline allowed. Tobias?
+ [x] Link qwen 3 coder to CLine
+ [x] Prelominary tests
	+ [x] local ollama: qwen3 30b
	+ [x] increase context? Yes -> 48k
+ [ ] Read docs
	+ [x] first scan (.clineignore!)
	+ [ ] Full
+ [ ] Understand inner working

# Key points local
32 GiB VRAM. As long as model does not spill to RAM/CPU, quite good.
Models , see also [Which local models actually work with Cline? AMD tested them all - Cline Blog](https://cline.bot/blog/local-models-amd)
- Qwen3 coder 30b
	- model from Ollama site probably Q4-1, maybe Q4_K_M, not Q4_0.
	  Up to 48k context seems safe & usable. Spilling / OOM at 54/56
	- See also [unsloth/Qwen3-Coder-30B-A3B-Instruct-GGUF at main](https://huggingface.co/unsloth/Qwen3-Coder-30B-A3B-Instruct-GGUF/tree/main) for smaller/larger
	- Test [Intel/Qwen3-Coder-30B-A3B-Instruct-gguf-q2ks-mixed-AutoRound at main](https://huggingface.co/Intel/Qwen3-Coder-30B-A3B-Instruct-gguf-q2ks-mixed-AutoRound/tree/main) 
	  Can reliably work on 80k context. 88k maybe, but mem slowly filling up, could lead to OOM later.

# Ollama integration

> [!NOTE] Warning
> When defining model file: Ollama model file don't "inherit" from parent FROM --> if changes needed (e.g. num_ctx), take definition from official model (/show model) and build on that. 

E.g., this is not enough:
```
FROM qwen3coder:30b
PARAMETER num_ctx 65536
```
Do this (Oct 2025):
```
FROM qwen3-coder:30b
PARAMETER num_ctx 49152

# Rest from original model file
TEMPLATE {{ .Prompt }}
RENDERER qwen3-coder
PARSER qwen3-coder
PARAMETER repeat_penalty 1.05
PARAMETER stop <|im_start|>
PARAMETER stop <|im_end|>
PARAMETER stop <|endoftext|>
PARAMETER temperature 0.7
PARAMETER top_k 20
PARAMETER top_p 0.8
```

