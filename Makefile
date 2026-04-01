# --- HANYA UNTUK BERSIH-BERSIH & PUSH ---

# 1. Bersihkan semua 'hutan' folder biar rapi
clean:
	@echo "Membersihkan sampah build..."
	rm -rf obj/ bin/ gen/ output/ *.dex jniLibs/
	@if [ -d "rust_logic" ]; then cd rust_logic && cargo clean; fi

# 2. Langsung kirim ke GitHub (Biar GitHub Actions yang build)
push:
	@echo "Mengirim ke GitHub..."
	git add .
	git commit -m "style: apply brik ui and hello rust"
	git push origin feature-ai-ui

# 3. Sekali perintah: Bersihkan lalu Push
deploy: clean push

.PHONY: clean push deploy
