import { shallowMount } from "@vue/test-utils";
import Debug from "@/components/Debug.vue";

describe("Debug.vue", () => {
  it("renders props.msg when passed", () => {
    const msg = "new message";
    const wrapper = shallowMount(Debug, {
      propsData: { msg }
    });
    expect(wrapper.text()).toMatch(msg);
  });
});
