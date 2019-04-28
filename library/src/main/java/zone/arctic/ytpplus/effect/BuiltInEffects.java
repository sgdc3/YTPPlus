package zone.arctic.ytpplus.effect;

public enum BuiltInEffects {
	RANDOM_SOUND(),
	RANDOM_SOUND_MUTE_OG(),
	REVERSE_CLIP(),
	SPEED_UP_CLIP_NO_PITCH(),
	SLOW_DOWN_CLIP_NO_PITCH(),
	CHORUS_AUDIO(),
	VIBRATO_AUDIO(),
	SPEED_UP_CLIP_WITH_PITCH(),
	SLOW_DOWN_CLIP_WITH_PITCH(),
	DANCE(),
	SQUIDWARD();

	private Effect effect;

	BuiltInEffects(Effect effect) {
		this.effect = effect;
	}

	public Effect getEffect() {
		return effect;
	}
}
