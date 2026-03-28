<template>
  <div class="cyber-select" :class="{ open: isOpen, disabled }" ref="selectRef">
    <div class="cyber-select__trigger" @click="toggle">
      <span class="cyber-select__value">{{ displayValue }}</span>
      <span class="cyber-select__arrow">
        <svg width="12" height="12" viewBox="0 0 12 12" fill="currentColor">
          <path d="M2.5 4.5L6 8L9.5 4.5" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>
        </svg>
      </span>
    </div>
    <Transition name="dropdown">
      <div v-if="isOpen" class="cyber-select__dropdown">
        <div
          v-for="opt in options"
          :key="opt.value"
          class="cyber-select__option"
          :class="{ selected: opt.value === modelValue }"
          @click="select(opt.value)"
        >
          {{ opt.label }}
        </div>
        <div v-if="clearable && modelValue" class="cyber-select__option cyber-select__clear" @click="clear">
          清除选择
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

interface Option {
  label: string
  value: any
}

const props = withDefaults(defineProps<{
  modelValue?: any
  options: Option[]
  placeholder?: string
  clearable?: boolean
  disabled?: boolean
}>(), {
  placeholder: '请选择',
  clearable: false,
  disabled: false,
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void
  (e: 'change', value: any): void
}>()

const isOpen = ref(false)
const selectRef = ref<HTMLElement>()

const displayValue = computed(() => {
  const opt = props.options.find(o => o.value === props.modelValue)
  return opt ? opt.label : props.placeholder
})

const toggle = () => {
  if (!props.disabled) isOpen.value = !isOpen.value
}

const select = (value: any) => {
  emit('update:modelValue', value)
  emit('change', value)
  isOpen.value = false
}

const clear = () => {
  emit('update:modelValue', undefined)
  emit('change', undefined)
  isOpen.value = false
}

const handleClickOutside = (e: MouseEvent) => {
  if (selectRef.value && !selectRef.value.contains(e.target as Node)) {
    isOpen.value = false
  }
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onUnmounted(() => document.removeEventListener('click', handleClickOutside))
</script>

<style scoped lang="scss">
.cyber-select {
  position: relative;
  min-width: 140px;
  font-family: 'Rajdhani', sans-serif;

  &.disabled {
    opacity: 0.5;
    pointer-events: none;
  }
}

.cyber-select__trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  background: var(--cyber-card);
  border: 1px solid var(--cyber-border);
  border-radius: var(--radius);
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-primary);

  &:hover {
    border-color: var(--neon-cyan);
    box-shadow: var(--glow-sm);
  }
}

.cyber-select.open .cyber-select__trigger {
  border-color: var(--neon-cyan);
  box-shadow: var(--glow-sm);
}

.cyber-select__value {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cyber-select__arrow {
  color: var(--text-muted);
  transition: transform 0.2s ease;
  display: flex;
  align-items: center;
}

.cyber-select.open .cyber-select__arrow {
  transform: rotate(180deg);
  color: var(--neon-cyan);
}

.cyber-select__dropdown {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  background: var(--cyber-card);
  border: 1px solid var(--cyber-border-strong);
  border-radius: var(--radius);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
  z-index: 1000;
  max-height: 240px;
  overflow-y: auto;
}

.cyber-select__option {
  padding: 10px 14px;
  cursor: pointer;
  transition: all 0.15s ease;
  color: var(--text-primary);

  &:hover {
    background: rgba(0, 243, 255, 0.1);
    color: var(--neon-cyan);
  }

  &.selected {
    color: var(--neon-cyan);
    background: rgba(0, 243, 255, 0.08);
  }
}

.cyber-select__clear {
  border-top: 1px solid var(--cyber-border);
  color: var(--text-muted);
  font-size: 13px;

  &:hover {
    color: var(--neon-red);
    background: rgba(239, 68, 68, 0.1);
  }
}

.dropdown-enter-active, .dropdown-leave-active {
  transition: all 0.2s ease;
}
.dropdown-enter-from, .dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
