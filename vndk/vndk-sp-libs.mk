VNDK_SP_LIBRARIES := \
    android.hidl.memory@1.0 \
    libRSCpuRef \
    libRSDriver \
    libRS_internal \
    libbcinfo \
    libblas \
    libcompiler_rt \
    libft2 \
    libhidlmemory \
    libion \
    libpng \

ifndef BOARD_VNDK_VERSION

VNDK_SP_LIBRARIES += \
    android.hardware.graphics.allocator@2.0 \
    android.hardware.graphics.mapper@2.0 \
    android.hardware.graphics.common@1.0 \
    android.hardware.renderscript@1.0 \
    libhwbinder \
    libbase \
    libcutils \
    libhardware \
    libutils \
    libc++ \
    libbacktrace \
    libunwind \
    libunwindstack \
    liblzma \
    libhidlbase \
    libhidltransport \

endif
