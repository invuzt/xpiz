#!/bin/bash

# Folder yang ingin diperiksa (tambahkan jika ada folder lain)
TARGET_DIRS=("src" "jni" "rust_logic" "res")
# File di root yang ingin diikutkan
ROOT_FILES=("AndroidManifest.xml" "Dummy.java" "Makefile" "build.sh")

# 1. Cetak file-file yang ada di root dulu
for f in "${ROOT_FILES[@]}"; do
    if [ -f "$f" ]; then
        echo "========================================"
        echo " FILE: $f"
        echo "========================================"
        cat "$f"
        echo -e "\n"
    fi
done

# 2. Cari semua file kode di dalam folder target secara otomatis
# Kita filter ekstensinya agar file gambar (.png) tidak ikut tercetak (bikin terminal berantakan)
find "${TARGET_DIRS[@]}" -type f -regextype posix-extended \
-regex ".*\.(rs|java|toml|yml|c|mk|sh|xml)" 2>/dev/null | while read -r file; do
    echo "========================================"
    echo " FILE: $file"
    echo "========================================"
    cat "$file"
    echo -e "\n"
done

