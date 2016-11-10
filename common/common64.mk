$(call inherit-product, device/oneplus/oneplus3/common/base.mk)

$(call inherit-product-if-exists, $(QCPATH)/common/config/device-vendor.mk)

PRODUCT_PROPERTY_OVERRIDES += \
    ro.vendor.extension_library=libqti-perfd-client.so \
    persist.radio.apm_sim_not_pwdn=1 \
    persist.radio.sib16_support=1 \
    persist.radio.custom_ecc=1
