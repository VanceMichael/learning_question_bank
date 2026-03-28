<template>
  <div class="cyber-dropdown-item" :class="{ danger }" @click="handleClick">
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import { inject } from 'vue'

defineProps<{
  danger?: boolean
}>()

const emit = defineEmits<{
  (e: 'click'): void
}>()

const closeDropdown = inject<() => void>('closeDropdown')

const handleClick = () => {
  emit('click')
  closeDropdown?.()
}
</script>

<style scoped lang="scss">
.cyber-dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.15s ease;
  color: var(--text-primary);
  font-family: 'Rajdhani', sans-serif;
  font-size: 14px;

  &:hover {
    background: rgba(0, 243, 255, 0.1);
    color: var(--neon-cyan);
  }

  &.danger:hover {
    background: rgba(239, 68, 68, 0.1);
    color: var(--neon-red);
  }
}
</style>
